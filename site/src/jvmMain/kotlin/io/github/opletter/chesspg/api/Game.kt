package io.github.opletter.chesspg.api

import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.stream.ApiStream
import io.github.opletter.chesspg.*
import io.github.opletter.chesspg.chessbackend.move
import io.github.opletter.chesspg.models.ChessStreamEvent
import io.github.opletter.chesspg.models.customGameState
import kotlinx.serialization.json.Json
import java.util.*

val GameStream = object : ApiStream() {
    override suspend fun onClientConnected(ctx: ClientConnectedContext) {
        ctx.logger.debug("User connected: ${ctx.clientId}")
    }

    override suspend fun onTextReceived(ctx: TextReceivedContext) {
        val database = ctx.data.getValue<Database>()

        when (val event = Json.decodeFromString<ChessStreamEvent.ClientSide>(ctx.text)) {
            is ChessStreamEvent.LookingForGame -> {
                if (database.lookingForGame.isEmpty()) {
                    database.lookingForGame.add(ctx.clientId)
                } else {
                    val other = database.lookingForGame.first()
                    database.lookingForGame.remove(other)

                    val players = listOf(ctx.clientId, other).shuffled()
                    val newGame = ChessGame(players[0], players[1])

                    database.registerGame(newGame)

                    ctx.stream.send(ChessStreamEvent.GameFound(newGame.getRole(ctx.clientId)))
                    ctx.stream.sendTo(ChessStreamEvent.GameFound(newGame.getRole(other)), listOf(other))
                }
            }

            is ChessStreamEvent.Move -> {
                val game = database.getGame(ctx.clientId) ?: error("game not found for ${ctx.clientId}")
                game.gameBackend.move(event.from, event.to)
                game.lastMove = event.from to event.to

                val gameState = game.gameBackend.customGameState(game.lastMove)

                if (game.gameBackend.isGameOver) {
                    database.unregisterGame(game)

                    val reason = when {
                        game.gameBackend.isStalemate -> ChessStreamEvent.GameOver.Reason.Stalemate
                        game.gameBackend.isThreefoldRepetition -> ChessStreamEvent.GameOver.Reason.ThreefoldRepetition
                        game.gameBackend.isInsufficientMaterial -> ChessStreamEvent.GameOver.Reason.InsufficientMaterial
                        game.gameBackend.isCheckmate -> ChessStreamEvent.GameOver.Reason.Checkmate
                        else -> error("Game over but no reason found")
                    }
                    val winner = game.gameBackend.winner

                    ctx.stream.sendTo(
                        value = ChessStreamEvent.GameOver(
                            gameId = game.id.toString(),
                            state = gameState,
                            reason = reason,
                            winner = winner?.toString(),
                        ),
                        clientIds = game.watchers + game.white + game.black,
                    )
                } else {
                    ctx.stream.sendTo(
                        value = ChessStreamEvent.GameStateUpdate(state = gameState, gameId = game.id.toString()),
                        clientIds = game.watchers + game.white + game.black,
                    )
                }
            }

            is ChessStreamEvent.UnwatchGame -> {
                val game = database.games[UUID.fromString(event.gameId)] ?: return
                game.watchers.remove(ctx.clientId)
            }

            is ChessStreamEvent.WatchGame -> {
                val game = database.games[UUID.fromString(event.gameId)] ?: return
                game.watchers.add(ctx.clientId)

                ctx.stream.sendTo(
                    ChessStreamEvent.GameStateUpdate(
                        state = game.gameBackend.customGameState(game.lastMove),
                        gameId = game.id.toString(),
                    ),
                    game.watchers
                )
            }
        }
    }

    override suspend fun onClientDisconnected(ctx: ClientDisconnectedContext) {
        val database = ctx.data.getValue<Database>()

        database.lookingForGame.remove(ctx.clientId)

        val game = database.getGame(ctx.clientId) ?: return
        database.unregisterGame(game)

        ctx.stream.sendTo(
            value = ChessStreamEvent.GameOver(
                game.id.toString(),
                game.gameBackend.customGameState(game.lastMove),
                ChessStreamEvent.GameOver.Reason.PlayerLeft,
                if (game.white == ctx.clientId) "Black" else "White",
            ),
            clientIds = game.watchers + game.white + game.black,
        )
    }
}