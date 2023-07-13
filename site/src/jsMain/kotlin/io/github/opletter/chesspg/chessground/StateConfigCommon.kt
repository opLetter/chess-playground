@file:JsModule("chessground")
@file:JsNonModule

package io.github.opletter.chesspg.chessground

sealed external interface Highlight {
    val lastMove: Boolean
    val check: Boolean
}

sealed external interface Animation {
    val enabled: Boolean
    val duration: Number
}

sealed external interface Movable {
    val free: Boolean
    val color: ChessColor?
    val dests: Dests?
    val showDests: Boolean

    sealed interface Events {
        val after: ((orig: String, dest: String, metadata: MoveMetadata) -> Unit)?
        val afterNewPiece: ((role: Role, key: String, metadata: MoveMetadata) -> Unit)?
    }

    val events: Events?
    val rookCastle: Boolean
}

sealed external interface Premovable {
    val enabled: Boolean?
    val showDests: Boolean?
    val castle: Boolean?
    val dests: List<String>?

    sealed interface Events {
        val set: ((orig: String, dest: String, metadata: SetPremoveMetadata) -> Unit)?
        val unset: (() -> Unit)?
    }

    val events: Events?
}

sealed external interface Predroppable {
    val enabled: Boolean?

    sealed interface Events {
        val set: ((role: Role, key: String) -> Unit)?
        val unset: (() -> Unit)?
    }

    val events: Events?
}

sealed external interface Draggable {
    val enabled: Boolean?
    val distance: Number?
    val autoDistance: Boolean?
    val showGhost: Boolean?
    val deleteOnDropOff: Boolean?
}

sealed external interface Selectable {
    val enabled: Boolean?
}

sealed external interface Events {
    val change: (() -> Unit)?
    val move: ((orig: String, dest: String, capturedPiece: Piece?) -> Unit)?
    val dropNewPiece: ((piece: Piece, key: String) -> Unit)?
    val select: ((key: String) -> Unit)?
    val insert: ((elements: Elements) -> Unit)?
}

// "ConfigDrawable" defined here because "Drawable" ("StateDrawable") is used elsewhere as well
sealed external interface ConfigDrawable {
    val enabled: Boolean?
    val visible: Boolean?
    val defaultSnapToValidMove: Boolean?
    val eraseOnClick: Boolean?
    val shapes: List<dynamic>? // todo: DrawShape
    val autoShapes: List<dynamic>? // todo: DrawShape
    val brushes: dynamic // todo: DrawBrushes?
    val onChange: ((shapes: List<dynamic>) -> Unit)?
}

sealed external interface Drawable : ConfigDrawable {
    override val enabled: Boolean
    override val visible: Boolean
    override val defaultSnapToValidMove: Boolean
    override val eraseOnClick: Boolean
    override val shapes: List<dynamic> // todo: DrawShape
    override val autoShapes: List<dynamic> // todo: DrawShape
    val current: dynamic // todo: DrawCurrent
    override val brushes: dynamic // todo: DrawBrushes

    // onChange not overriden as it can in fact be null
    val prevSvgHash: String
}