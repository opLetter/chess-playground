package io.github.opletter.chesspg.models

import io.github.opletter.chess.Game
import io.github.opletter.chess.toFen
import kotlinx.serialization.Serializable

@Serializable
sealed interface ChessStreamEvent {
    @Serializable
    sealed interface ClientSide : ChessStreamEvent

    @Serializable
    sealed interface ServerSide : ChessStreamEvent

    @Serializable
    object LookingForGame : ClientSide

    @Serializable
    data class GameFound(val color: String) : ServerSide

    @Serializable
    data class Move(
        val from: String,
        val to: String,
    ) : ClientSide

    @Serializable
    data class GameStateUpdate(
        val state: CustomGameState,
        val gameId: String? = null,
    ) : ServerSide

    @Serializable
    data class WatchGame(val gameId: String) : ClientSide

    @Serializable
    data class GameOver(
        val gameId: String,
        val state: CustomGameState,
        val reason: Reason,
        val winner: String?,
    ) : ServerSide {
        enum class Reason {
            PlayerLeft, Checkmate, Stalemate, ThreefoldRepetition, InsufficientMaterial, FiftyMoves
        }
    }
}


fun ChessStreamEvent.GameOver.Reason.toMessage(): String {
    return when (this) {
        ChessStreamEvent.GameOver.Reason.PlayerLeft -> "A player left the game."
        ChessStreamEvent.GameOver.Reason.Checkmate -> "Checkmate!"
        ChessStreamEvent.GameOver.Reason.Stalemate -> "Stalemate!"
        ChessStreamEvent.GameOver.Reason.ThreefoldRepetition -> "Threefold repetition."
        ChessStreamEvent.GameOver.Reason.InsufficientMaterial -> "Insufficient material."
        ChessStreamEvent.GameOver.Reason.FiftyMoves -> "Fifty moves without a capture or pawn move."
    }
}

@Serializable
class CustomGameState(
    val turnColor: String,
    val fen: String,
    val lastMove: Pair<String, String>?,
    val possibleMoves: Map<String, Array<String>>,
    val inCheck: Boolean = false,
)

fun Game.customGameState(lastMove: Pair<String, String>?): CustomGameState {
    return CustomGameState(
        turnColor = state.turn.toString(),
        fen = state.toFen().toString(),
        lastMove = lastMove,
        possibleMoves = getAllAvailableMovesAsStrings(),
        inCheck = state.inCheck,
    )
}

fun Game.getAllAvailableMovesAsStrings(): Map<String, Array<String>> {
    return state.allAvailableMoveTargets.entries.associate { (k, v) ->
        k.toString() to v.map { it.toString() }.toTypedArray()
    }
}