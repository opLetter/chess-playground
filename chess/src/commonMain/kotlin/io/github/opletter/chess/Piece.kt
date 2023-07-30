package io.github.opletter.chess

class Piece(val type: Type, val color: Color) {
    sealed interface Type {
        /** A piece that can be promoted to. */
        sealed interface Promotable : Type

        object King : Type
        object Pawn : Type
        object Queen : Promotable
        object Rook : Promotable
        object Bishop : Promotable
        object Knight : Promotable
    }

    override fun toString(): String {
        return "$type $color"
    }
}