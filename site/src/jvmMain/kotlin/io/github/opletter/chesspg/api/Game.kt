package io.github.opletter.chesspg.api

import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.stream.ApiStream
import io.github.opletter.chess.GameResult
import io.github.opletter.chess.Square
import io.github.opletter.chesspg.*
import io.github.opletter.chesspg.models.ChessStreamEvent
import io.github.opletter.chesspg.models.customGameState
import kotlinx.serialization.json.Json
import java.util.*

val GameStream = object : ApiStream() {
    override suspend fun onClientConnected(ctx: ClientConnectedContext) {
        val database = ctx.data.getValue<Database>()
        database.inMenu.add(ctx.clientId)
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
                    database.inMenu.remove(other)
                    database.inMenu.remove(ctx.clientId)

                    val players = listOf(ctx.clientId, other).shuffled()
                    val newGame = ChessGame(players[0], players[1])

                    database.registerGame(newGame)

                    ctx.stream.send(ChessStreamEvent.GameFound(newGame.getRole(ctx.clientId)))
                    ctx.stream.sendTo(ChessStreamEvent.GameFound(newGame.getRole(other)), listOf(other))
                }
            }

            is ChessStreamEvent.Move -> {
                val game = database.getGame(ctx.clientId) ?: error("game not found for ${ctx.clientId}")
                game.gameBackend.move(Square(event.from), Square(event.to))
                game.lastMove = event.from to event.to

                val gameState = game.gameBackend.customGameState(game.lastMove)
                val messageRecipients = game.watchers + game.white + game.black + database.inMenu

                val gameResult = game.gameBackend.result

                if (gameResult != null) {
                    database.unregisterGame(game)

                    val reason = when (gameResult) {
                        is GameResult.Checkmate -> ChessStreamEvent.GameOver.Reason.Checkmate
                        GameResult.FiftyMoves -> ChessStreamEvent.GameOver.Reason.FiftyMoves
                        GameResult.InsufficientMaterial -> ChessStreamEvent.GameOver.Reason.InsufficientMaterial
                        GameResult.Stalemate -> ChessStreamEvent.GameOver.Reason.Stalemate
                        GameResult.ThreefoldRepetition -> ChessStreamEvent.GameOver.Reason.ThreefoldRepetition
                    }
                    val winner = if (gameResult is GameResult.Checkmate) gameResult.winner else null

                    ctx.stream.sendTo(
                        value = ChessStreamEvent.GameOver(
                            gameId = game.id.toString(),
                            state = gameState,
                            reason = reason,
                            winner = winner?.toString(),
                        ),
                        clientIds = messageRecipients,
                    )
                } else {
                    ctx.stream.sendTo(
                        value = ChessStreamEvent.GameStateUpdate(state = gameState, gameId = game.id.toString()),
                        clientIds = messageRecipients,
                    )
                }
            }

            is ChessStreamEvent.WatchGame -> {
                val game = database.games[UUID.fromString(event.gameId)] ?: return
                database.inMenu.remove(ctx.clientId)
                game.watchers.add(ctx.clientId)

                ctx.stream.sendTo(
                    ChessStreamEvent.GameStateUpdate(
                        state = game.gameBackend.customGameState(game.lastMove),
                        gameId = game.id.toString(),
                    ),
                    game.watchers + database.inMenu,
                )
            }
        }
    }

    override suspend fun onClientDisconnected(ctx: ClientDisconnectedContext) {
        val database = ctx.data.getValue<Database>()

        database.lookingForGame.remove(ctx.clientId)
        database.inMenu.remove(ctx.clientId)

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