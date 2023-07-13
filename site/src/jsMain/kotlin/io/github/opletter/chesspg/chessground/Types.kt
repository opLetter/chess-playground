@file:JsModule("chessground")
@file:JsNonModule

package io.github.opletter.chesspg.chessground

import org.w3c.dom.DOMRectReadOnly
import org.w3c.dom.HTMLElement
import org.w3c.dom.svg.SVGElement

sealed external interface Piece {
    val role: Role
    val color: ChessColor
    val promoted: Boolean?
}

sealed external interface Drop {
    val role: Role
    val key: String // technically a type
}

sealed external interface Elements {
    val board: HTMLElement
    val wrap: HTMLElement
    val container: HTMLElement
    val ghost: HTMLElement?
    val svg: SVGElement?
    val customSvg: SVGElement?
    val autoPieces: HTMLElement?
}

sealed external interface Dom {
    val elements: Elements
    val bounds: Memo<DOMRectReadOnly>  // memo?
    fun redraw()
    fun redrawNow(skipSvg: Boolean? = definedExternally) // is it defined externall?
    val unbind: (() -> Unit)?
    val destroyed: Boolean?
}

sealed external interface Exploding {
    val stage: Number
    val keys: List<String> // technical List<Key>
}

sealed external interface MoveMetadata {
    val premove: Boolean
    val ctrlKey: Boolean?
    val holdTime: Number?
    val captured: Piece?
    val predrop: Boolean?
}

sealed external interface SetPremoveMetadata {
    val ctrlKey: Boolean?
}

sealed external class KeyedNode : HTMLElement {
    val cgKey: String // "Key"
}

sealed external class PieceNode : KeyedNode {
    override val tagName = definedExternally // "PIECE"
    val cgPiece: Piece
    val cgAnimating: Boolean?
    val cgFading: Boolean?
    val cgDragging: Boolean?
    val cgScale: Number?
}

sealed external class SquareNode : KeyedNode {
    override val tagName = definedExternally // "SQUARE"
}

sealed external interface Memo<T> {
    operator fun invoke(): T
    fun clear()
}

sealed external interface Timer {
    fun start()
    fun cancel()
    fun stop(): Number
}