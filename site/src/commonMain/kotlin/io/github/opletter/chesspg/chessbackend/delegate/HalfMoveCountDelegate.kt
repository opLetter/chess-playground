package io.github.opletter.chesspg.chessbackend.delegate

import io.github.opletter.chesspg.chessbackend.Move
import io.github.opletter.chesspg.chessbackend.Piece

class HalfMoveCountDelegate(
    currentMove: Int,
) {

    var currentMove: Int = currentMove
        private set

    fun update(move: Move) {
        when {
            move.isCapture == true -> {
                currentMove = 0
            }

            move is Move.GeneralMove && move.piece == Piece.Type.Pawn -> {
                currentMove = 0
            }

            move is Move.PromotionMove -> {
                currentMove = 0
            }

            else -> {
                currentMove++
            }
        }
    }

    fun check(): Boolean {
        return currentMove == 50
    }
}