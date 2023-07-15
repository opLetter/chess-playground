@file:Suppress("NAME_CONTAINS_ILLEGAL_CHARS", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")

package io.github.opletter.chesspg.chessground

import js.collections.JsMap
import js.core.jso
import js.core.tupleOf

typealias Pieces = JsMap<String, Piece> // <Key, Piece>
typealias Dests = JsMap<String, Array<String>> // <Key, List<Key>>
typealias PiecesDiff = JsMap<String, Piece?> // <Key, Piece | undefined>

@JsName("{blue: 'blue', green: 'green', red: 'red', yellow: 'yellow'}")
sealed external interface BrushColor {
    companion object {
        val blue: BrushColor
        val green: BrushColor
        val red: BrushColor
        val yellow: BrushColor
    }
}

@JsName("{left: 'left', right: 'right'}")
sealed external interface RanksPosition {
    companion object {
        val left: RanksPosition
        val right: RanksPosition
    }
}

@JsName("{White: 'white', Black: 'black'}")
sealed external interface ChessColor {
    companion object {
        val White: ChessColor
        val Black: ChessColor
    }
}

@JsName("{White: 'white', Black: 'black', Both: 'both', None: undefined}")
sealed external interface MovableColor {
    companion object {
        val White: MovableColor
        val Black: MovableColor
        val Both: MovableColor
        val None: MovableColor
    }
}

@JsName("{King: 'king', Queen: 'queen', Rook: 'rook', Bishop: 'bishop', Knight: 'knight', Pawn: 'pawn'}")
sealed external interface Role {
    companion object {
        val King: Role
        val Queen: Role
        val Rook: Role
        val Bishop: Role
        val Knight: Role
        val Pawn: Role
    }
}

fun <T, V> Map<T, V>.toJsMap(): JsMap<T, V> = JsMap(entries.map { tupleOf(it.key, it.value) }.toTypedArray())
fun <K, V> jsMapOf(vararg pairs: Pair<K, V>): JsMap<K, V> = mapOf(*pairs).toJsMap()
//    JsMap(pairs.map { tupleOf(it.first, it.second) }.toTypedArray())

class ChessgroundPiece(
    override val role: Role,
    override val color: ChessColor,
    override val promoted: Boolean? = null,
) : Piece

enum class CheckState(internal val value: dynamic) {
    White("white"),
    Black("black"),
    CurrentColor(true),
    None(false),
}

@Suppress("FunctionName")
// we sneakily access the `check` field through dynamic, since it's hidden in Kotlin
fun ConfigBuilder(config: SafeConfig.() -> Unit): Config = jso(config).apply {
    @Suppress("UnsafeCastFromDynamic")
    if (check.asDynamic() /* != undefined */) this.asDynamic().check = check.value
}

inline fun SafeConfig.HighlightBuilder(config: Config.MutableHighlight.() -> Unit): Config.SafeHighlight = jso(config)
inline fun SafeConfig.AnimationBuilder(config: Config.MutableAnimation.() -> Unit): Config.SafeAnimation = jso(config)
inline fun SafeConfig.MovableBuilder(config: Config.MutableMovable.() -> Unit): Config.SafeMovable = jso(config)
inline fun SafeConfig.PreMovableBuilder(config: Config.MutablePremovable.() -> Unit): Config.SafePremovable =
    jso(config)

inline fun SafeConfig.PreDroppableBuilder(config: Config.MutablePredroppable.() -> Unit): Config.SafePredroppable =
    jso(config)

inline fun SafeConfig.DraggableBuilder(config: Config.MutableDraggable.() -> Unit): Config.SafeDraggable = jso(config)
inline fun SafeConfig.EventsBuilder(config: Config.EventsContext.() -> Unit): Config.SafeEvents = jso(config)
inline fun SafeConfig.DrawableBuilder(config: Config.MutableDrawable.() -> Unit): Config.SafeDrawable = jso(config)

// region movable

inline fun Config.MutableMovable.EventsBuilder(
    config: Config.MutableMovable.EventsContext.() -> Unit,
): Config.MutableMovable.SafeEvents = jso(config)

class MovableAfterEvent(
    val orig: String,
    val dest: String,
    val metadata: MoveMetadata,
)

inline fun Config.MutableMovable.EventsContext.after(crossinline config: (MovableAfterEvent) -> Unit) {
    val events: Config.MutableMovable.MutableEvents = this
    events.after = { a, b, c -> config(MovableAfterEvent(a, b, c)) }
}

class MovableAfterNewPieceEvent(
    val role: Role,
    val key: String,
    val metadata: MoveMetadata,
)

inline fun Config.MutableMovable.EventsContext.afterNewPiece(crossinline config: (MovableAfterNewPieceEvent) -> Unit) {
    val events: Config.MutableMovable.MutableEvents = this
    events.afterNewPiece = { a, b, c -> config(MovableAfterNewPieceEvent(a, b, c)) }
}

// endregion

// region premovable

inline fun Config.MutablePremovable.EventsBuilder(
    config: Config.MutablePremovable.SafeEvents.() -> Unit,
): Config.MutablePremovable.SafeEvents = jso(config)

// purposely flatten ctrlKey structure
class PremovableSetEvent(
    val orig: String,
    val dest: String,
    val ctrlKey: Boolean?,
)

inline fun Config.MutablePremovable.EventsContext.set(crossinline config: (PremovableSetEvent) -> Unit) {
    val events: Config.MutablePremovable.MutableEvents = this
    events.set = { a, b, c -> config(PremovableSetEvent(a, b, c.ctrlKey)) }
}

inline fun Config.MutablePremovable.EventsContext.unset(noinline config: () -> Unit) {
    val events: Config.MutablePremovable.MutableEvents = this
    events.unset = config
}

// endregion

// region predroppable

inline fun Config.MutablePredroppable.EventsBuilder(
    config: Config.MutablePredroppable.SafeEvents.() -> Unit,
): Config.MutablePredroppable.SafeEvents = jso(config)

class PredroppableNewPieceEvent(
    val role: Role,
    val key: String,
)

inline fun Config.MutablePredroppable.EventsContext.set(crossinline config: (PredroppableNewPieceEvent) -> Unit) {
    val events: Config.MutablePredroppable.MutableEvents = this
    events.set = { a, b -> config(PredroppableNewPieceEvent(a, b)) }
}

inline fun Config.MutablePredroppable.EventsContext.unset(noinline config: () -> Unit) {
    val events: Config.MutablePredroppable.MutableEvents = this
    events.unset = config
}

// endregion

// region events

inline fun Config.EventsContext.change(noinline config: () -> Unit) {
    val events: Config.MutableEvents = this
    events.change = config
}

class MoveEvent(
    val orig: String,
    val dest: String,
    val capturedPiece: Piece?,
)

inline fun Config.EventsContext.move(crossinline config: (MoveEvent) -> Unit) {
    val events: Config.MutableEvents = this
    events.move = { a, b, c -> config(MoveEvent(a, b, c)) }
}

class DropNewPieceEvent(
    val piece: Piece,
    val key: String,
)

inline fun Config.EventsContext.dropNewPiece(crossinline config: (DropNewPieceEvent) -> Unit) {
    val events: Config.MutableEvents = this
    events.dropNewPiece = { a, b -> config(DropNewPieceEvent(a, b)) }
}

inline fun Config.EventsContext.select(noinline config: (String) -> Unit) {
    val events: Config.MutableEvents = this
    events.select = config
}

inline fun Config.EventsContext.insert(noinline config: (Elements) -> Unit) {
    val events: Config.MutableEvents = this
    events.insert = config
}

// endregion

@DslMarker
annotation class ConfigDsl