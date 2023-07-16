package io.github.opletter.chesspg.chessbackend.pgn

import io.github.opletter.chesspg.chessbackend.Move


data class PgnData(
    val moves: List<Move>,
)