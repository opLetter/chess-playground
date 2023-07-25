@file:JsModule("chessground")
@file:JsNonModule

package io.github.opletter.chesspg.chessground

import org.w3c.dom.HTMLElement

// all values are optional and `Config` is only passed, never returned, so `var`s work fine
@ConfigDsl
sealed external interface Config {
    /** chess position in Forsyth notation */
    var fen: String

    /** board orientation. white | black */
    var orientation: ChessColor

    /** turn to play. white | black */
    var turnColor: ChessColor

    /** true for current color, false to unset */
    @JsName("check")
    var dynamicCheck: dynamic

    /** squares part of the last move ["c3"; "c4"] */
    var lastMove: Array<String>

    /** square currently selected "a1" */
    var selected: String

    /** include coords attributes */
    var coordinates: Boolean

    /** immediately complete the castle by moving the rook after king move */
    var autoCastle: Boolean

    /** don't bind events: the user will never be able to move pieces around */
    var viewOnly: Boolean

    /** because who needs a context menu on a chessboard */
    var disableContextMenu: Boolean

    /** adds z-index values to pieces (for 3D) */
    var addPieceZIndex: Boolean

    /** add --cg-width and --cg-height CSS vars containing the board's dimensions to this element */
    var addDimensionsCssVarsTo: HTMLElement

    /** block scrolling via touch dragging on the board, e.g. for coordinate training */
    var blockTouchScroll: Boolean

    /** disable checking for human only input (e.isTrusted) */
    var trustAllEvents: Boolean

    @ConfigDsl
    sealed interface SafeHighlight
    sealed interface MutableHighlight : Highlight, SafeHighlight {
        override var lastMove: Boolean
        override var check: Boolean
    }

    var highlight: SafeHighlight

    @ConfigDsl
    sealed interface SafeAnimation
    sealed interface MutableAnimation : Animation, SafeAnimation {
        override var enabled: Boolean
        override var duration: Int
    }

    var animation: SafeAnimation

    @ConfigDsl
    sealed interface SafeMovable
    sealed interface MutableMovable : SafeMovable, Movable {
        override var free: Boolean
        override var color: MovableColor
        override var dests: Dests
        override var showDests: Boolean

        @ConfigDsl
        sealed interface MutableEvents : Movable.Events {
            override var after: (orig: String, dest: String, metadata: MoveMetadata) -> Unit
            override var afterNewPiece: (role: Role, key: String, metadata: MoveMetadata) -> Unit
        }

        sealed interface SafeEvents : MutableEvents {
            @Deprecated("We don't like this", level = DeprecationLevel.HIDDEN)
            override var after: (orig: String, dest: String, metadata: MoveMetadata) -> Unit

            @Deprecated("We don't like this", level = DeprecationLevel.HIDDEN)
            override var afterNewPiece: (role: Role, key: String, metadata: MoveMetadata) -> Unit
        }

        sealed interface EventsContext : SafeEvents

        override var events: SafeEvents
        override var rookCastle: Boolean
    }

    var movable: SafeMovable

    @ConfigDsl
    sealed interface SafePremovable
    sealed interface MutablePremovable : Premovable, SafePremovable {
        override var enabled: Boolean
        override var showDests: Boolean
        override var castle: Boolean
        override var dests: List<String>
        override var customDests: List<String>

        @ConfigDsl
        sealed interface MutableEvents : Premovable.Events {
            override var set: (orig: String, dest: String, metadata: SetPremoveMetadata) -> Unit
            override var unset: () -> Unit
        }

        sealed interface SafeEvents : MutableEvents {
            @Deprecated("We don't like this", level = DeprecationLevel.HIDDEN)
            override var set: (orig: String, dest: String, metadata: SetPremoveMetadata) -> Unit

            @Deprecated("We don't like this", level = DeprecationLevel.HIDDEN)
            override var unset: () -> Unit
        }

        sealed interface EventsContext : SafeEvents

        override var events: SafeEvents
    }

    var premovable: SafePremovable

    @ConfigDsl
    sealed interface SafePredroppable
    sealed interface MutablePredroppable : Predroppable, SafePredroppable {
        override var enabled: Boolean

        @ConfigDsl
        sealed interface MutableEvents : Predroppable.Events {
            override var set: (role: Role, key: String) -> Unit
            override var unset: () -> Unit
        }

        sealed interface SafeEvents : MutableEvents {
            @Deprecated("We don't like this", level = DeprecationLevel.HIDDEN)
            override var set: (role: Role, key: String) -> Unit

            @Deprecated("We don't like this", level = DeprecationLevel.HIDDEN)
            override var unset: () -> Unit
        }

        sealed interface EventsContext : SafeEvents

        override var events: SafeEvents
    }

    var predroppable: SafePredroppable

    @ConfigDsl
    sealed interface SafeDraggable
    sealed interface MutableDraggable : Draggable, SafeDraggable {
        override var enabled: Boolean
        override var distance: Number
        override var autoDistance: Boolean
        override var showGhost: Boolean
        override var deleteOnDropOff: Boolean
    }

    var draggable: SafeDraggable

    @ConfigDsl
    sealed interface SafeSelectable
    sealed interface MutableSelectable : Selectable, SafeSelectable {
        override var enabled: Boolean
    }

    var selectable: SafeSelectable

    @ConfigDsl
    sealed interface MutableEvents : Events {
        override var change: () -> Unit
        override var move: (orig: String, dest: String, capturedPiece: Piece?) -> Unit
        override var dropNewPiece: (piece: Piece, key: String) -> Unit
        override var select: (key: String) -> Unit
        override var insert: (elements: Elements) -> Unit
    }

    sealed interface SafeEvents : MutableEvents {
        @Deprecated("We don't like this", level = DeprecationLevel.HIDDEN)
        override var change: () -> Unit

        @Deprecated("We don't like this", level = DeprecationLevel.HIDDEN)
        override var move: (orig: String, dest: String, capturedPiece: Piece?) -> Unit

        @Deprecated("We don't like this", level = DeprecationLevel.HIDDEN)
        override var dropNewPiece: (piece: Piece, key: String) -> Unit

        @Deprecated("We don't like this", level = DeprecationLevel.HIDDEN)
        override var select: (key: String) -> Unit

        @Deprecated("We don't like this", level = DeprecationLevel.HIDDEN)
        override var insert: (elements: Elements) -> Unit
    }

    sealed interface EventsContext : SafeEvents

    var events: SafeEvents

    @ConfigDsl
    sealed interface SafeDrawable
    sealed interface MutableDrawable : ConfigDrawable, SafeDrawable {
        override var enabled: Boolean?
        override var visible: Boolean?
        override var defaultSnapToValidMove: Boolean?
        override var eraseOnClick: Boolean?
        override var shapes: List<dynamic>? // todo: DrawShape
        override var autoShapes: List<dynamic>? // todo: DrawShape
        override var brushes: dynamic // todo: DrawBrushes?
        override var onChange: ((shapes: List<dynamic>) -> Unit)?
    }

    var drawable: SafeDrawable
}


// consider just having this in Config?
external interface SafeConfig : Config {
    @JsName("publicCheck") // tried ignoring instead but that doesn't work
    var check: CheckState

    @Deprecated("We don't like dynamics", level = DeprecationLevel.HIDDEN)
    override var dynamicCheck: dynamic
}