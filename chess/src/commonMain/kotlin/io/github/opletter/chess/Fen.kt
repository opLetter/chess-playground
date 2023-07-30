package io.github.opletter.chess

import kotlin.jvm.JvmInline

@JvmInline
value class Fen(val value: String) {
    init {
        val parts = value.split(" ")

        require(parts.size == 6) { "FEN must have 6 parts" }
        require(parts[0].count { it == '/' } == 7) { "FEN must have 8 ranks" }
        require(parts[1] == "w" || parts[1] == "b") { "FEN must have a valid active color (got: ${parts[1]})" }

        val castleChars = parts[2].groupBy { it }
        val validCastleState = parts[2] == "-" ||
                (castleChars.keys.all { it == 'K' || it == 'Q' || it == 'k' || it == 'q' }
                        && castleChars.values.all { it.size == 1 })
        require(validCastleState) { "FEN must have a valid castle state (got: ${parts[2]})" }

        require(parts[3] == "-" || Square.ofOrNull(parts[3]) != null) {
            "FEN must have a valid en passant square (got: ${parts[3]})"
        }

        require(parts[4].toIntOrNull() != null) { "FEN must have a valid halfmove clock (got: ${parts[4]})" }

        require(parts[5].toIntOrNull() != null) { "FEN must have a valid move count (got: ${parts[5]})" }
    }

    override fun toString(): String = value
}

val Fen.placement: String
    get() = value.split(" ").first()

fun Fen.toGameState(): GameState {
    val parts = value.split(" ")
    val piecePlacement = parts[0].split("/").mapIndexed { rank, row ->
        row.flatMap { char ->
            char.digitToIntOrNull()?.let { emptySquares -> List(emptySquares) { ' ' } } ?: listOf(char)
        }.mapIndexedNotNull { file, c ->
            val pieceType = when (c.lowercaseChar()) {
                'p' -> Piece.Type.Pawn
                'r' -> Piece.Type.Rook
                'n' -> Piece.Type.Knight
                'b' -> Piece.Type.Bishop
                'q' -> Piece.Type.Queen
                'k' -> Piece.Type.King
                else -> null
            }
            val piece = pieceType?.let { Piece(it, if (c.isUpperCase()) Color.White else Color.Black) }
            piece?.let { Square(7 - rank, file) to it }
        }
    }.flatten().toMap()

    val turn = when (parts[1]) {
        "w" -> Color.White
        "b" -> Color.Black
        else -> error("Invalid turn")
    }
    val whiteCastleRights = when {
        parts[2].contains('K') && parts[2].contains('Q') -> CastleRights.Both
        parts[2].contains('K') -> CastleRights.KingSide
        parts[2].contains('Q') -> CastleRights.QueenSide
        else -> CastleRights.None
    }
    val blackCastleRights = when {
        parts[2].contains('k') && parts[2].contains('q') -> CastleRights.Both
        parts[2].contains('k') -> CastleRights.KingSide
        parts[2].contains('q') -> CastleRights.QueenSide
        else -> CastleRights.None
    }
    val enPassantTarget = parts[3].let { if (it == "-") null else Square(it) }
    val halfMoveClock = parts[4].toInt()
    val moveNumber = parts[5].toInt()

    return GameState(
        piecePlacement,
        turn,
        whiteCastleRights,
        blackCastleRights,
        enPassantTarget,
        halfMoveClock,
        moveNumber,
    )
}