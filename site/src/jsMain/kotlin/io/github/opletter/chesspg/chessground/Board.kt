@file:JsModule("chessground")
@file:JsNonModule

package io.github.opletter.chesspg.chessground


external fun toggleOrientation(state: HeadlessState)

external fun reset(state: HeadlessState)

external fun setPieces(state: HeadlessState, pieces: Pieces)

external fun setCheck(state: HeadlessState, color: ChessColor)
external fun setCheck(state: HeadlessState, color: Boolean)

// much more