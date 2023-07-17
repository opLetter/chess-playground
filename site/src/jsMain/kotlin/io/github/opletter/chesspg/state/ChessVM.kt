package io.github.opletter.chesspg.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.streams.ApiStream
import com.varabyte.kobweb.streams.connect
import io.github.opletter.chesspg.chessground.*
import io.github.opletter.chesspg.models.ChessStreamEvent
import io.github.opletter.chesspg.models.CustomGameState
import io.github.opletter.chesspg.models.toMessage
import io.github.opletter.chesspg.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

@Stable
class ChessVM(private val coroutineScope: CoroutineScope) {
    private val stream = ApiStream("game")

    private val defaultMenuState = ClientState.Menu(
        watch = { stream.send(ChessStreamEvent.WatchGame(it)) },
        unwatch = { stream.send(ChessStreamEvent.UnwatchGame(it)) },
    )

    var state: ClientState by mutableStateOf(defaultMenuState)
        private set

    private fun handleMessage(str: String) {
        val event = Json.decodeFromString<ChessStreamEvent>(str)

        when (val state = state) {
            is ClientState.LookingForGame -> {
                check(event is ChessStreamEvent.GameFound)
                this.state = ClientState.Playing(
                    playerColor = if (event.color == "white") ChessColor.White else ChessColor.Black,
                    afterMove = { stream.send(ChessStreamEvent.Move(it.orig, it.dest)) }
                )
            }

            is ClientState.Menu -> {
                if (event is ChessStreamEvent.GameStateUpdate) {
                    coroutineScope.launch {
                        while (state.activeGames[event.gameId]?.boardApi == null) {
                            delay(0.5.seconds)
                        }
                        val api = state.activeGames[event.gameId]?.boardApi ?: error("game api not found in menu")
                        api.set(ConfigBuilder {
                            applyGameState(event.state)
                            animation = AnimationBuilder { enabled = api.state.lastMove != null }
                        })
                    }
                }
                if (event is ChessStreamEvent.GameOver) {
                    state.activeGames = state.activeGames - event.gameId
                }
            }

            is ClientState.Playing -> {
                if (event is ChessStreamEvent.GameStateUpdate) {
                    val api = state.controller.boardApi ?: return
                    api.set(ConfigBuilder {
                        applyGameState(event.state)
                        movable = MovableBuilder {
                            dests = event.state.possibleMoves.toJsMap()
                        }
                    })
                    api.playPremove()
                }
                if (event is ChessStreamEvent.GameOver) {
                    state.controller.boardApi?.set(ConfigBuilder {
                        applyGameState(event.state)
                        movable = MovableBuilder { color = MovableColor.None }
                    })
                    val mainMessage = when (event.winner?.lowercase()) {
                        null -> "Draw!"
                        state.playerColor.toString() -> "You won!"
                        else -> "You lost!"
                    }
                    val info = event.reason
                        .takeIf { it != ChessStreamEvent.GameOver.Reason.PlayerLeft }
                        ?.toMessage() ?: "Your opponent left the game."

                    state.bannerMessage = "$mainMessage $info"
                }
            }

            is ClientState.Watching -> {
                if (event is ChessStreamEvent.GameStateUpdate) {
                    // TODO: deduplify w/menu?
                    coroutineScope.launch {
                        while (state.controller.boardApi == null) {
                            delay(0.5.seconds)
                        }
                        val api = state.controller.boardApi ?: error("game api not found in menu")
                        api.set(ConfigBuilder {
                            applyGameState(event.state)
                            animation = AnimationBuilder { enabled = api.state.lastMove != null }
                        })
                    }
                }
                if (event is ChessStreamEvent.GameOver) {
                    val mainMessage = event.winner?.let { "$it wins!" } ?: "Draw!"
                    val info = event.reason.toMessage()

                    state.bannerMessage = "$mainMessage $info"
                }
            }
        }
    }

    init {
        coroutineScope.launch {
            stream.connect { ctx -> handleMessage(ctx.text) }
        }
    }

    fun lookForGame() {
        state = ClientState.LookingForGame
        stream.send(ChessStreamEvent.LookingForGame)
    }

    fun watchGame(id: String, fen: String? = null) {
        stream.send(ChessStreamEvent.WatchGame(id))
        state = ClientState.Watching(fen)
    }

    fun backToMenu() {
        state = defaultMenuState
    }
}

fun SafeConfig.applyGameState(state: CustomGameState) {
    fen = state.fen
    state.lastMove?.let { lastMove = it.toList().toTypedArray() }
    turnColor = if (state.turnColor == "White") ChessColor.White else ChessColor.Black
    check = if (state.inCheck) CheckState.CurrentColor else CheckState.None
}