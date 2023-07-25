@file:JsModule("chessground")
@file:JsNonModule

package io.github.opletter.chesspg.chessground

import js.core.JsTuple2

sealed external interface Api {
    /** reconfigure the instance. Accepts all config options, except for viewOnly & drawable.visible.
     * board will be animated accordingly, if animations are enabled.
     */
    fun set(config: Config)

    /** read chessground state; write at your own risks. */
    val state: State

    /** get the position as a FEN string (only contains pieces, no flags)
     * e.g. rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR
     */
    fun getFen(): String

    /** change the view angle */
    fun toggleOrientation()

    /** perform a move programmatically */
    fun move(orig: String, dest: String)

    /** add and/or remove arbitrary pieces on the board */
    fun setPieces(pieces: PiecesDiff)

    /** click a square programmatically */
    fun selectSquare(key: String?, force: Boolean?)

    /** put a new piece on the board */
    fun newPiece(piece: Piece, key: String)

    /** play the current premove, if any; returns true if premove was played */
    fun playPremove(): Boolean

    /** cancel the current premove, if any */
    fun cancelPremove()

    /** play the current predrop, if any; returns true if premove was played */
    fun playPredrop(validate: (drop: Drop) -> Boolean): Boolean

    /** cancel the current predrop, if any */
    fun cancelPredrop()

    /** cancel the current move being made */
    fun cancelMove()

    /** cancel current move and prevent further ones */
    fun stop()

    /** make squares explode (atomic chess) */
    fun explode(keys: List<String>)

    /** programmatically draw user shapes */
    fun setShapes(shapes: List<dynamic>) // TODO: DrawShape

    /** programmatically draw auto shapes */
    fun setAutoShapes(shapes: List<dynamic>) // TODO: DrawShape

    /** square name at this DOM position (like "e4") */
    fun getKeyAtDomPos(pos: JsTuple2<Number, Number>): String?

    /** only useful when CSS changes the board width/height ratio (for 3D) */
    fun redrawAll(): State

    /** for crazyhouse and board editors */
    fun dragNewPiece(piece: Piece, event: dynamic, force: Boolean?) // TODO: MouchEvent

    /** unbinds all events
     * (important for document-wide events like scroll and mousemove)
     */
    fun destroy(): dynamic // TODO: Unbind
}