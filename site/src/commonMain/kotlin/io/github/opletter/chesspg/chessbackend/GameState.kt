package io.github.opletter.chesspg.chessbackend

import io.github.opletter.chesspg.chessbackend.delegate.CastlingDelegate

internal class GameState(
    val board: Board,
    val isWhiteTurn: Boolean,
    val whiteCastleState: CastlingDelegate.CastleState,
    val blackCastleState: CastlingDelegate.CastleState,
    val enPassantSquare: Square?,
    val halfMoveCount: Int,
    val moveCount: Int,
) {
    val currentColor: Piece.Color
        get() = if (isWhiteTurn) Piece.Color.White else Piece.Color.Black
}