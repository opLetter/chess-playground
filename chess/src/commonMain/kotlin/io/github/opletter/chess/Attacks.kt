package io.github.opletter.chess

import kotlin.math.sign

internal fun Map<Square, Piece>.getAttackedSquares(square: Square): List<Square> {
    val piece = this[square] ?: return emptyList()
    return when (piece.type) {
        Piece.Type.Pawn -> listOfNotNull(
            square.forward(piece.color)?.left(),
            square.forward(piece.color)?.right(),
        )

        Piece.Type.King -> square.baseKingMoves()
        Piece.Type.Knight -> square.knightMoves()
        Piece.Type.Bishop -> bishopMoves(square, piece.color, this, true)

        Piece.Type.Queen -> {
            bishopMoves(square, piece.color, this, true) +
                    rookMoves(square, piece.color, this, true)
        }

        Piece.Type.Rook -> rookMoves(square, piece.color, this, true)
    }
}

internal fun Map<Square, Piece>.getAllAttackedSquares(attackedBy: Color): List<Square> {
    return flatMap { (square, piece) ->
        if (piece.color == attackedBy) getAttackedSquares(square) else emptyList()
    }
}

internal fun Map<Square, Piece>.getCheckSquares(attackedBy: Color, kingSquare: Square): Set<Square> {
    return filter { (square, piece) ->
        piece.color == attackedBy && kingSquare in getAttackedSquares(square)
    }.keys
}

private val RangedPieces = listOf(Piece.Type.Bishop, Piece.Type.Rook, Piece.Type.Queen)

internal fun kingDangerSquares(
    kingSquare: Square,
    attackerSquare: Square,
    piecePlacement: Map<Square, Piece>,
): List<Square> {
    val attackerType = piecePlacement[attackerSquare]?.type ?: error("No piece at $attackerSquare")
    if (attackerType !in RangedPieces)
        return emptyList()

    val dx = kingSquare.file - attackerSquare.file
    val dy = kingSquare.rank - attackerSquare.rank

    return generateSequence(attackerSquare) { it.moved(dy.sign, dx.sign) }.drop(1).toList()
}

internal fun kingBlockSquares(
    kingSquare: Square,
    attackerSquare: Square,
    piecePlacement: Map<Square, Piece>,
): List<Square> {
    val attackerType = piecePlacement[attackerSquare]?.type ?: error("No piece at $attackerSquare")
    if (attackerType !in RangedPieces)
        return emptyList()

    val dx = kingSquare.file - attackerSquare.file
    val dy = kingSquare.rank - attackerSquare.rank

    return generateSequence(attackerSquare) { it.moved(dy.sign, dx.sign) }
        .takeWhile { piecePlacement[it]?.type != Piece.Type.King }
        .toList()
}

private fun squaresToKing(attackerSquare: Square, kingSquare: Square): List<Square>? {
    val dx = kingSquare.file - attackerSquare.file
    val dy = kingSquare.rank - attackerSquare.rank

    return generateSequence(attackerSquare) { square ->
        square.moved(dy.sign, dx.sign).takeIf { it != kingSquare }
    }.toList().takeIf { it.lastOrNull()?.moved(dy.sign, dx.sign) == kingSquare }
}

// list of pinned pieces and the squares along which they can move (aka the axis along which the king is)
internal fun pinnedPieces(kingSquare: Square, piecePlacement: Map<Square, Piece>): List<Pair<Square, List<Square>>> {
    val enemyColor = piecePlacement[kingSquare]!!.color.opposite()

    return piecePlacement.mapNotNull { (square, piece) ->
        if (piece.color != enemyColor || piece.type !in RangedPieces) return@mapNotNull null

        val line = squaresToKing(square, kingSquare) ?: return@mapNotNull null
        val piecesBetween = line.drop(1).filter { it in piecePlacement } // drop first since that's the attacking piece

        piecesBetween.singleOrNull()?.let { it to line }
    }
}

internal fun Square.baseKingMoves(): List<Square> = listOfNotNull(
    moved(1, 0),
    moved(-1, 0),
    moved(0, 1),
    moved(0, -1),
    moved(1, 1),
    moved(1, -1),
    moved(-1, 1),
    moved(-1, -1),
)

internal fun Square.knightMoves(): List<Square> = listOfNotNull(
    moved(1, 2),
    moved(2, 1),
    moved(1, -2),
    moved(-2, 1),
    moved(-1, 2),
    moved(2, -1),
    moved(-1, -2),
    moved(-2, -1),
)

internal inline fun getSquaresInDirection(
    baseSquare: Square,
    color: Color,
    piecePlacement: Map<Square, Piece>,
    includeSameColorBoundary: Boolean,
    nextSquare: Square.() -> Square?,
): List<Square> = buildList {
    var curSquare: Square? = baseSquare
    do {
        curSquare = curSquare?.nextSquare()
        if (curSquare != null && (includeSameColorBoundary || piecePlacement[curSquare]?.color != color))
            add(curSquare)
    } while (curSquare != null && curSquare !in piecePlacement)
}


internal fun bishopMoves(
    baseSquare: Square,
    color: Color,
    piecePlacement: Map<Square, Piece>,
    includeSameColorBoundary: Boolean,
): List<Square> {
    val directions: List<Square.() -> Square?> = listOf(
        { moved(1, 1) },
        { moved(1, -1) },
        { moved(-1, 1) },
        { moved(-1, -1) },
    )
    return directions.flatMap {
        getSquaresInDirection(baseSquare, color, piecePlacement, includeSameColorBoundary, it)
    }
}

internal fun rookMoves(
    baseSquare: Square,
    color: Color,
    piecePlacement: Map<Square, Piece>,
    includeSameColorBoundary: Boolean,
): List<Square> {
    val directions: List<Square.() -> Square?> = listOf(
        { moved(1, 0) },
        { moved(-1, 0) },
        { moved(0, 1) },
        { moved(0, -1) },
    )
    return directions.flatMap {
        getSquaresInDirection(baseSquare, color, piecePlacement, includeSameColorBoundary, it)
    }
}