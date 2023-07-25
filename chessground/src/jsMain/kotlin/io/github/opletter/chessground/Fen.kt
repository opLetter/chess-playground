package io.github.opletter.chessground

@JsModule("chessground/fen")
@JsNonModule
external object Fen {
    fun read(fen: String): Pieces
}