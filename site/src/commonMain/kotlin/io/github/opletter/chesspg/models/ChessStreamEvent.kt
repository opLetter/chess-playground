package io.github.opletter.chesspg.models

import io.github.opletter.chesspg.chessbackend.Game
import io.github.opletter.chesspg.chessbackend.fen.FenSerializer
import io.github.opletter.chesspg.chessbackend.getAllAvailableMovesAsStrings
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
            PlayerLeft, Checkmate, Stalemate, ThreefoldRepetition, InsufficientMaterial
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
        turnColor = state.currentColor.toString(),
        fen = FenSerializer.serialize(state),
        lastMove = lastMove,
        possibleMoves = getAllAvailableMovesAsStrings(),
        inCheck = isCheck
    )
}