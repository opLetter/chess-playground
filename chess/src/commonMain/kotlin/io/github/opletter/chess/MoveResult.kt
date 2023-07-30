package io.github.opletter.chess

sealed interface MoveResult {
    sealed interface Successful : MoveResult

    object Success : Successful
    class GameOver(val result: GameResult) : Successful

    class Illegal(val reason: String) : MoveResult
}