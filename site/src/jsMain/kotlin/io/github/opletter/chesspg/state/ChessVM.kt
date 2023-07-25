package io.github.opletter.chesspg.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.streams.ApiStream
import com.varabyte.kobweb.streams.connect
import io.github.opletter.chessground.*
import io.github.opletter.chesspg.components.widgets.ChessController
import io.github.opletter.chesspg.models.ChessStreamEvent
import io.github.opletter.chesspg.models.CustomGameState
import io.github.opletter.chesspg.models.toMessage
import io.github.opletter.chesspg.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.milliseconds

@Stable
class ChessVM(private val coroutineScope: CoroutineScope) {
    private val stream = ApiStream("game")

    var state: ClientState by mutableStateOf(ClientState.Menu())
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
                    state.activeGames[event.gameId]?.runWhenBoardApiAvailable {
                        it.set(ConfigBuilder {
                            applyGameState(event.state)
                            animation = AnimationBuilder { enabled = it.state.lastMove != null }
                        })
                    }
                }
                if (event is ChessStreamEvent.GameOver) {
                    state.activeGames -= event.gameId
                }
            }

            is ClientState.Playing -> {
                if (event is ChessStreamEvent.GameStateUpdate) {
                    state.controller.runWhenBoardApiAvailable {
                        it.set(ConfigBuilder {
                            applyGameState(event.state)
                            movable = MovableBuilder {
                                dests = event.state.possibleMoves.toJsMap()
                            }
                        })
                        it.playPremove()
                    }
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
                    state.controller.runWhenBoardApiAvailable {
                        it.set(ConfigBuilder {
                            applyGameState(event.state)
                            animation = AnimationBuilder { enabled = it.state.lastMove != null }
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

    private inline fun ChessController.runWhenBoardApiAvailable(crossinline block: (Api) -> Unit) {
        val api = boardApi
        if (api != null) {
            block(api)
            return
        }
        coroutineScope.launch {
            while (boardApi == null) {
                delay(200.milliseconds)
            }
            block(boardApi!!)
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
        state = ClientState.Menu()
    }
}

fun SafeConfig.applyGameState(state: CustomGameState) {
    fen = state.fen
    state.lastMove?.let { lastMove = it.toList().toTypedArray() }
    turnColor = if (state.turnColor == "White") ChessColor.White else ChessColor.Black
    check = if (state.inCheck) CheckState.CurrentColor else CheckState.None
}