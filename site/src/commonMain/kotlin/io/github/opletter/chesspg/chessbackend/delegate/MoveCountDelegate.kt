package io.github.opletter.chesspg.chessbackend.delegate

import io.github.opletter.chesspg.chessbackend.Piece

class MoveCountDelegate(
    currentMove: Int,
) {

    var currentMove: Int = currentMove
        private set

    fun update(color: Piece.Color) {
        if (color == Piece.Color.Black) {
            currentMove++
        }
    }
}