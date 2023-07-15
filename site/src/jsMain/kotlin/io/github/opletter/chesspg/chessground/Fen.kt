package io.github.opletter.chesspg.chessground

@JsModule("chessground/fen")
@JsNonModule
external object Fen {
    fun read(fen: String): Pieces
}