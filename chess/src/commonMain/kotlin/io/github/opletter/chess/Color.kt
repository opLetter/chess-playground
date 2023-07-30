package io.github.opletter.chess

enum class Color {
    White, Black
}

fun Color.opposite(): Color {
    return when (this) {
        Color.White -> Color.Black
        Color.Black -> Color.White
    }
}