package io.github.opletter.chess

sealed class Move(val from: Square, val to: Square) {
    class Normal(from: Square, to: Square) : Move(from, to)
    class EnPassant(from: Square, to: Square) : Move(from, to)
    class Promotion(from: Square, to: Square, val promotion: Piece.Type.Promotable) : Move(from, to)
    class Castle(from: Square, to: Square, val side: CastleSide) : Move(from, to)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Move

        if (from != other.from) return false
        return to == other.to
    }

    override fun hashCode(): Int {
        var result = from.hashCode()
        result = 31 * result + to.hashCode()
        return result
    }

    override fun toString(): String {
        return "Move(from=$from, to=$to)"
    }
}