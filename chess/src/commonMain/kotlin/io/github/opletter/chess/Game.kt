package io.github.opletter.chess

class Game(initialState: GameState = GameState.Initial) {
    var state: GameState = initialState
        private set

    /**
     * Map of FEN strings to the number of times they've been seen.
     * Used to detect threefold repetition.
     *
     * Note that this uses just the placement part of the FEN string, not the whole thing.
     */
    private val positionTracker: MutableMap<String, Int> = mutableMapOf(initialState.toFen().placement to 1)

    val result: GameResult?
        get() = when {
            state.checkmate -> GameResult.Checkmate(state.turn.opposite())
            state.stalemate -> GameResult.Stalemate
            state.halfMoveClock == 100 -> GameResult.FiftyMoves
            positionTracker[state.toFen().placement] == 3 -> GameResult.ThreefoldRepetition
            else -> null
        }

    fun move(from: Square, to: Square, promotionPiece: Piece.Type.Promotable? = null): MoveResult {
        val piece = state.piecePlacement[from] ?: return MoveResult.Illegal("No piece at $from")
        if (piece.type == Piece.Type.King && from.rank == Rank.homeRank(state.turn).index && from.file == File.E.index) {
            if (to.file == File.G.index) {
                return move(Move.Castle(from, to, CastleSide.KingSide))
            } else if (to.file == File.C.index) {
                return move(Move.Castle(from, to, CastleSide.QueenSide))
            }
        }
        if (piece.type == Piece.Type.Pawn && to == state.enPassantTarget) {
            return move(Move.EnPassant(from, to))
        }
        if (piece.type == Piece.Type.Pawn && to.rank == Rank.homeRank(state.turn.opposite()).index && promotionPiece != null) {
            return move(Move.Promotion(from, to, promotionPiece))
        }
        return move(Move.Normal(from, to))
    }

    fun move(move: Move): MoveResult {
        if (move !in state.allAvailableMoves) {
            return MoveResult.Illegal("Move $move is not available")
        }

        val newPieces = state.piecePlacement.toMutableMap()
        val movedPiece = newPieces.remove(move.from) ?: error("No piece at ${move.from}")

        when (move) {
            is Move.Normal -> newPieces[move.to] = movedPiece

            is Move.Promotion -> newPieces[move.to] = Piece(move.promotion, state.turn)

            is Move.EnPassant -> {
                newPieces[move.to] = movedPiece
                newPieces.remove(state.enPassantTarget!!.backward(state.turn))
            }

            is Move.Castle -> {
                newPieces[move.to] = movedPiece
                when (move.side) {
                    CastleSide.KingSide -> {
                        val rook = newPieces.remove(move.to.right()!!) ?: error("No rook during kingside castle")
                        newPieces[move.to.left()!!] = rook
                    }

                    CastleSide.QueenSide -> {
                        val rook = newPieces.remove(move.to.moved(0, -2)!!)
                            ?: error("No rook during queenside castle")
                        newPieces[move.to.right()!!] = rook
                    }
                }
            }
        }

        fun CastleRights.updated(): CastleRights {
            return when {
                this == CastleRights.None || movedPiece.type == Piece.Type.King -> CastleRights.None

                move is Move.Castle -> {
                    if (this != CastleRights.Both) {
                        CastleRights.None
                    } else {
                        when (move.side) {
                            CastleSide.KingSide -> CastleRights.QueenSide
                            CastleSide.QueenSide -> CastleRights.KingSide
                        }
                    }
                }

                move.from == Square(Rank.homeRank(state.turn), File.A) -> {
                    if (this == CastleRights.Both) CastleRights.KingSide else CastleRights.None
                }

                move.from == Square(Rank.homeRank(state.turn), File.H) -> {
                    if (this == CastleRights.Both) CastleRights.QueenSide else CastleRights.None
                }

                else -> this
            }
        }

        val movedPieceIsPawn = movedPiece.type == Piece.Type.Pawn
        val enPassantTarget = if (
            movedPieceIsPawn && move.from.isPawnHome(state.turn) &&
            move.to == move.from.forward(state.turn)?.forward(state.turn)
        ) move.from.forward(state.turn) else null

        state = GameState(
            piecePlacement = newPieces,
            turn = state.turn.opposite(),
            whiteCastleRights = state.whiteCastleRights.let { if (state.turn == Color.White) it.updated() else it },
            blackCastleRights = state.blackCastleRights.let { if (state.turn == Color.Black) it.updated() else it },
            enPassantTarget = enPassantTarget,
            halfMoveClock = if (movedPieceIsPawn || move.to in state.piecePlacement) 0 else state.halfMoveClock + 1,
            moveNumber = if (state.turn == Color.Black) state.moveNumber + 1 else state.moveNumber,
        )

        val fenPlacement = state.toFen().placement
        positionTracker[fenPlacement] = positionTracker.getOrElse(fenPlacement) { 0 } + 1

        return result?.let { MoveResult.GameOver(it) } ?: MoveResult.Success
    }
}