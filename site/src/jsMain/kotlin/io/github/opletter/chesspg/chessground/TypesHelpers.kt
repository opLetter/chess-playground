@file:Suppress("NAME_CONTAINS_ILLEGAL_CHARS", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")

package io.github.opletter.chesspg.chessground

import js.collections.JsMap
import js.core.jso
import js.core.tupleOf

typealias Pieces = Map<String, Piece> // <Key, Piece>
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

fun SafeConfig.HighlightBuilder(config: Config.MutableHighlight.() -> Unit): Config.SafeHighlight = jso(config)
fun SafeConfig.AnimationBuilder(config: Config.MutableAnimation.() -> Unit): Config.SafeAnimation = jso(config)
fun SafeConfig.MovableBuilder(config: Config.MutableMovable.() -> Unit): Config.SafeMovable = jso(config)
fun SafeConfig.PreMovableBuilder(config: Config.MutablePremovable.() -> Unit): Config.SafePremovable = jso(config)
fun SafeConfig.PreDroppableBuilder(config: Config.MutablePredroppable.() -> Unit): Config.SafePredroppable = jso(config)
fun SafeConfig.DraggableBuilder(config: Config.MutableDraggable.() -> Unit): Config.SafeDraggable = jso(config)
fun SafeConfig.EventsBuilder(config: Config.MutableEvents.() -> Unit): Config.SafeEvents = jso(config)
fun SafeConfig.DrawableBuilder(config: Config.MutableDrawable.() -> Unit): Config.SafeDrawable = jso(config)

fun Config.MutableMovable.EventsBuilder(
    config: Config.MutableMovable.MutableEvents.() -> Unit,
): Config.MutableMovable.SafeEvents = jso(config)

fun Config.MutablePremovable.EventsBuilder(
    config: Config.MutablePremovable.MutableEvents.() -> Unit,
): Config.MutablePremovable.SafeEvents = jso(config)

fun Config.MutablePredroppable.EventsBuilder(
    config: Config.MutablePredroppable.MutableEvents.() -> Unit,
): Config.MutablePredroppable.SafeEvents = jso(config)

@DslMarker
annotation class ConfigDsl