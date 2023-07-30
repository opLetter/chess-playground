package io.github.opletter.chess

sealed interface GameResult {
    class Checkmate(val winner: Color) : GameResult

    sealed interface Draw : GameResult

    object Stalemate : Draw
    object FiftyMoves : Draw
    object ThreefoldRepetition : Draw
    object InsufficientMaterial : Draw
}