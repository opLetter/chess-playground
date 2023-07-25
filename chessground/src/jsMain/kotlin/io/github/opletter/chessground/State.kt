@file:JsModule("chessground")
@file:JsNonModule

package io.github.opletter.chessground

import org.w3c.dom.HTMLElement

sealed external interface HeadlessState {
    val pieces: Pieces

    /** board orientation. white | black */
    val orientation: ChessColor

    /** turn to play. white | black */
    val turnColor: ChessColor

    /** square currently in check "a2" */
    val check: String? // Key

    /** squares part of the last move ["c3"; "c4"] */
    val lastMove: List<String>? // Key[]

    /** square currently selected "a1" */
    val selected: String? // Key

    /** include coords attributes */
    val coordinates: Boolean

    /** position ranks on either side. left | right */
    val ranksPosition: RanksPosition

    /** immediately complete the castle by moving the rook after king move */
    val autoCastle: Boolean

    /** don't bind events: the user will never be able to move pieces around */
    val viewOnly: Boolean

    /** because who needs a context menu on a chessboard */
    val disableContextMenu: Boolean

    /** adds z-index values to pieces (for 3D) */
    val addPieceZIndex: Boolean

    /** add --cg-width and --cg-height CSS vars containing the board's dimensions to this element */
    val addDimensionsCssVarsTo: HTMLElement?

    /** block scrolling via touch dragging on the board, e.g. for coordinate training */
    val blockTouchScroll: Boolean

    /** add a data-key attribute to piece elements */
    val pieceKey: Boolean

    /** disable checking for human only input (e.isTrusted) */
    val trustAllEvents: Boolean?

    val highlight: Highlight

    sealed interface StateAnimation : Animation {
        val current: dynamic // todo: AnimCurrent
    }

    val animation: StateAnimation

    val movable: Movable

    sealed interface StatePremovable : Premovable {
        override val enabled: Boolean
        override val showDests: Boolean
        override val castle: Boolean
        val current: Pair<String, String> // KeyPair = [Key, Key] // todo: this probably doesn't workq
    }

    val premovable: StatePremovable

    sealed interface StatePredroppable : Predroppable {
        override val enabled: Boolean

        sealed interface Current {
            val role: Role
            val key: String // Key
        }

        val current: Current?

        override val events: Predroppable.Events // not nullable/optional
    }

    val predroppable: Predroppable

    sealed interface StateDraggable : Draggable {
        override val enabled: Boolean
        override val distance: Number
        override val autoDistance: Boolean
        override val showGhost: Boolean
        override val deleteOnDropOff: Boolean
        val current: dynamic // todo: DragCurrent
    }

    val draggable: StateDraggable

    sealed interface DropMode {
        val active: Boolean
        val piece: Piece?
    }

    val dropmode: DropMode

    sealed interface StateSelectable : Selectable {
        override val enabled: Boolean
    }

    val selectable: StateSelectable

    sealed interface Stats {
        val dragged: Boolean
        val ctrlKey: Boolean?
    }

    val stats: Stats

    val events: Events
    val drawable: Drawable
    val exploding: Exploding?
    val hold: Timer
}

sealed external interface State : HeadlessState {
    val dom: Dom
}

external fun defaults(): HeadlessState