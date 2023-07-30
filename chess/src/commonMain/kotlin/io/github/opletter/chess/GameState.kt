package io.github.opletter.chess

class GameState(
    val piecePlacement: Map<Square, Piece>,
    val turn: Color,
    val whiteCastleRights: CastleRights,
    val blackCastleRights: CastleRights,
    val enPassantTarget: Square?,
    val halfMoveClock: Int,
    val moveNumber: Int,
) {

    private val kingSquare by lazy {
        piecePlacement.entries.first { it.value.type == Piece.Type.King && it.value.color == turn }.key
    }
    private val checkSquares by lazy {
        piecePlacement.getCheckSquares(turn.opposite(), kingSquare)
    }
    private val pinnedPieces by lazy {
        pinnedPieces(kingSquare, piecePlacement)
    }
    private val dangerSquares by lazy {
        checkSquares.flatMap { kingDangerSquares(kingSquare, it, piecePlacement) }
    }
    private val blockSquares by lazy {
        checkSquares.flatMap { kingBlockSquares(kingSquare, it, piecePlacement) }
    }
    private val attackedSquares by lazy {
        piecePlacement.getAllAttackedSquares(turn.opposite())
    }

    val inCheck: Boolean
        get() = checkSquares.isNotEmpty()

    val checkmate: Boolean
        get() = inCheck && allAvailableMoves.isEmpty()

    val stalemate: Boolean
        get() = !inCheck && allAvailableMoves.isEmpty()

    val allAvailableMoves: List<Move> by lazy {
        piecePlacement.keys.flatMap { availableMoves(it) }
    }

    val allAvailableMoveTargets: Map<Square, List<Square>> by lazy {
        allAvailableMoves.groupBy({ it.from }, { it.to })
    }

    fun availableMoveTargets(square: Square): List<Square> {
        return availableMoves(square).map { it.to }
    }

    fun availableMoves(square: Square): List<Move> {
        return when (checkSquares.size) {
            0 -> availableMovesNonCheck(square)
            1 -> {
                val potentialMoves = availableMovesNonCheck(square)
                val checkSquare = checkSquares.single()

                potentialMoves.filter {
                    if (it.from == kingSquare) {
                        it.to !in dangerSquares && it !is Move.Castle
                    } else {
                        it.to in blockSquares || it.to == checkSquare
                    }
                }
            }

            else -> {
                if (square == kingSquare) {
                    square.baseKingMoves().mapNotNull {
                        if (piecePlacement[it]?.color != turn && it !in attackedSquares && it !in dangerSquares)
                            Move.Normal(square, it)
                        else null
                    }
                } else emptyList()
            }
        }
    }

    private fun availableMovesNonCheck(square: Square): List<Move> {
        val piece = piecePlacement[square]?.takeIf { it.color == turn } ?: return emptyList()

        val generalMoves: List<Move> = when (piece.type) {
            Piece.Type.King -> buildList {
                square.baseKingMoves().forEach {
                    if (piecePlacement[it]?.color != piece.color && it !in attackedSquares)
                        add(Move.Normal(square, it))
                }

                val castleRights = when (turn) {
                    Color.White -> whiteCastleRights
                    Color.Black -> blackCastleRights
                }

                if (castleRights == CastleRights.Both || castleRights == CastleRights.KingSide) {
                    val passThroughSquares = listOf(
                        square.right(),
                        square.moved(0, 2),
                    )
                    if (passThroughSquares.all { it !in piecePlacement && it !in attackedSquares }) {
                        add(Move.Castle(square, square.moved(0, 2)!!, CastleSide.KingSide))
                    }
                }
                if (castleRights == CastleRights.Both || castleRights == CastleRights.QueenSide) {
                    val passThroughSquares = listOf(
                        square.left(),
                        square.moved(0, -2),
                    )
                    if (passThroughSquares.all { it !in piecePlacement && it !in attackedSquares }) {
                        add(Move.Castle(square, square.moved(0, -2)!!, CastleSide.QueenSide))
                    }
                }
            }

            Piece.Type.Pawn -> buildList {
                val forward = square.forward(piece.color)

                if (forward != null && forward !in piecePlacement) {
                    add(Move.Normal(square, forward))

                    val forwardTwo = forward.forward(piece.color)

                    if (forwardTwo != null && forwardTwo !in piecePlacement && square.isPawnHome(piece.color)) {
                        add(Move.Normal(square, forwardTwo))
                    }
                }

                listOfNotNull(forward?.right(), forward?.left()).forEach {
                    if (piecePlacement[it]?.color == piece.color.opposite())
                        add(Move.Normal(square, it))
                    else if (enPassantTarget == it)
                        add(Move.EnPassant(square, it))
                }
            }

            Piece.Type.Bishop -> bishopMoves(square, piece.color, piecePlacement, false)
                .map { Move.Normal(square, it) }

            Piece.Type.Knight -> {
                square.knightMoves().mapNotNull {
                    if (piecePlacement[it]?.color != piece.color)
                        Move.Normal(square, it)
                    else null
                }
            }

            Piece.Type.Queen -> {
                val bishopMoves = bishopMoves(square, piece.color, piecePlacement, false)
                val rookMoves = rookMoves(square, piece.color, piecePlacement, false)

                (bishopMoves + rookMoves).map { Move.Normal(square, it) }
            }

            Piece.Type.Rook -> rookMoves(square, piece.color, piecePlacement, false)
                .map { Move.Normal(square, it) }
        }

        return generalMoves.filter { move ->
            pinnedPieces.none { (pinnedSquare, pinnedAxis) ->
                move.from == pinnedSquare && move.to !in pinnedAxis
            }
        }
    }

    companion object {
        val Initial = GameState(
            piecePlacement = InitialPiecePlacement,
            turn = Color.White,
            whiteCastleRights = CastleRights.Both,
            blackCastleRights = CastleRights.Both,
            enPassantTarget = null,
            halfMoveClock = 0,
            moveNumber = 1,
        )
    }
}

val InitialPiecePlacement = buildMap {
    File.values().forEach { file ->
        put(Square(Rank.Two, file), Piece(Piece.Type.Pawn, Color.White))
        put(Square(Rank.Seven, file), Piece(Piece.Type.Pawn, Color.Black))
    }
    put(Square(Rank.One, File.A), Piece(Piece.Type.Rook, Color.White))
    put(Square(Rank.One, File.B), Piece(Piece.Type.Knight, Color.White))
    put(Square(Rank.One, File.C), Piece(Piece.Type.Bishop, Color.White))
    put(Square(Rank.One, File.D), Piece(Piece.Type.Queen, Color.White))
    put(Square(Rank.One, File.E), Piece(Piece.Type.King, Color.White))
    put(Square(Rank.One, File.F), Piece(Piece.Type.Bishop, Color.White))
    put(Square(Rank.One, File.G), Piece(Piece.Type.Knight, Color.White))
    put(Square(Rank.One, File.H), Piece(Piece.Type.Rook, Color.White))
    put(Square(Rank.Eight, File.A), Piece(Piece.Type.Rook, Color.Black))
    put(Square(Rank.Eight, File.B), Piece(Piece.Type.Knight, Color.Black))
    put(Square(Rank.Eight, File.C), Piece(Piece.Type.Bishop, Color.Black))
    put(Square(Rank.Eight, File.D), Piece(Piece.Type.Queen, Color.Black))
    put(Square(Rank.Eight, File.E), Piece(Piece.Type.King, Color.Black))
    put(Square(Rank.Eight, File.F), Piece(Piece.Type.Bishop, Color.Black))
    put(Square(Rank.Eight, File.G), Piece(Piece.Type.Knight, Color.Black))
    put(Square(Rank.Eight, File.H), Piece(Piece.Type.Rook, Color.Black))
}

fun GameState.toFen(): Fen {
    fun CastleRights.toFenStringUppercase(): String {
        return when (this) {
            CastleRights.None -> ""
            CastleRights.KingSide -> "K"
            CastleRights.QueenSide -> "Q"
            CastleRights.Both -> "KQ"
        }
    }

    val fenPiecePlacement = List(8) { 7 - it }.joinToString("/") { x ->
        List(8) { it }.joinToString("") { y ->
            val piece = piecePlacement[Square(x, y)]
            if (piece != null) {
                when (piece.type) {
                    Piece.Type.Pawn -> "p"
                    Piece.Type.Knight -> "n"
                    Piece.Type.Bishop -> "b"
                    Piece.Type.Rook -> "r"
                    Piece.Type.Queen -> "q"
                    Piece.Type.King -> "k"
                }.let { if (piece.color == Color.White) it.uppercase() else it }
            } else "1"
        }
    }.replace("1{2,}".toRegex()) { it.value.length.toString() }

    val fenTurn = when (turn) {
        Color.White -> "w"
        Color.Black -> "b"
    }

    val fenWhiteCastleRights = whiteCastleRights.toFenStringUppercase()
    val fenBlackCastleRights = blackCastleRights.toFenStringUppercase().lowercase()
    val fenCastlingRights = (fenWhiteCastleRights + fenBlackCastleRights).ifEmpty { "-" }

    val fenEnPassantTarget = enPassantTarget?.toString() ?: "-"

    return Fen("$fenPiecePlacement $fenTurn $fenCastlingRights $fenEnPassantTarget $halfMoveClock $moveNumber")
}