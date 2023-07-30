package io.github.opletter.chess

import kotlin.jvm.JvmInline

@JvmInline
value class Square internal constructor(private val index: Int) {
    init {
        require(index in 0..63) { "Square index must be between 0 and 63, inclusive" }
    }

    internal val rank: Int
        get() = index / 8

    internal val file: Int
        get() = index % 8

    override fun toString(): String = "${'a' + file}${rank + 1}"

    companion object {
        internal operator fun invoke(rank: Int, file: Int): Square {
            return Square(rank * 8 + file)
        }

        operator fun invoke(rank: Rank, file: File): Square {
            return invoke(rank.index, file.index)
        }

        operator fun invoke(value: String): Square {
            require(value.length == 2) { "Square string must be 2 characters long" }
            require(value[0] in 'a'..'h') { "Square string must start with a letter a-h" }
            require(value[1] in '1'..'8') { "Square string must end with a number between 1-8" }
            return invoke(value[1].digitToInt() - 1, value[0] - 'a')
        }

        fun ofOrNull(value: String): Square? {
            return try {
                invoke(value)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}

internal fun Square.moved(up: Int, right: Int): Square? {
    val newRank = rank + up
    val newFile = file + right
    return if (newRank in 0..7 && newFile in 0..7) {
        Square(newRank, newFile)
    } else {
        null
    }
}

internal fun Square.forward(color: Color): Square? =
    moved(if (color == Color.White) 1 else -1, 0)

internal fun Square.backward(color: Color): Square? = forward(color.opposite())

internal fun Square.left(): Square? = moved(0, -1)

internal fun Square.right(): Square? = moved(0, 1)

internal fun Square.isPawnHome(color: Color): Boolean = when (color) {
    Color.White -> rank == 1
    Color.Black -> rank == 6
}

enum class Rank(internal val index: Int) {
    One(0), Two(1), Three(2), Four(3), Five(4), Six(5), Seven(6), Eight(7);

    internal companion object {
        internal fun homeRank(color: Color): Rank = when (color) {
            Color.White -> One
            Color.Black -> Eight
        }
    }
}

enum class File(internal val index: Int) {
    A(0), B(1), C(2), D(3), E(4), F(5), G(6), H(7);
}