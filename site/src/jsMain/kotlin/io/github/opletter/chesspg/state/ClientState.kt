package io.github.opletter.chesspg.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.opletter.chesspg.chessbackend.Game
import io.github.opletter.chesspg.chessbackend.getAllAvailableMovesAsStrings
import io.github.opletter.chesspg.chessground.*
import io.github.opletter.chesspg.components.widgets.ChessController

sealed interface ClientState {
    sealed class ActiveGame : ClientState {
        var bannerMessage by mutableStateOf<String?>(null)

        abstract val controller: ChessController
    }

    class Menu(val watch: (String) -> Unit, val unwatch: (String) -> Unit) : ClientState {
        var activeGames by mutableStateOf(emptyMap<String, ChessController>())
    }

    object LookingForGame : ClientState

    class Playing(val playerColor: ChessColor, afterMove: (MovableAfterEvent) -> Unit) : ActiveGame() {
        override val controller = ChessController(ConfigBuilder {
            orientation = playerColor
            movable = MovableBuilder {
                color = if (playerColor == ChessColor.White) MovableColor.White else MovableColor.Black
                free = false
                // TODO: have this as a predefined constant?
                dests = Game.create()!!.getAllAvailableMovesAsStrings().toJsMap()
                events = EventsBuilder {
                    after { afterMove(it) }
                }
            }
        })
    }

    class Watching(initialFen: String? = null) : ActiveGame() {
        override val controller = ChessController(ConfigBuilder {
            initialFen?.let { fen = it }
            // we don't set viewOnly to true because we want to be able to draw
            animation = AnimationBuilder { enabled = false } // for initial setup
            drawable = DrawableBuilder { enabled = true }
            movable = MovableBuilder {
                this.color = MovableColor.None
            }
        })
    }
}