package io.github.opletter.chesspg.components.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.browser.api
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.OverflowWrap
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.SpanText
import io.github.opletter.chesspg.chessground.AnimationBuilder
import io.github.opletter.chesspg.chessground.ConfigBuilder
import io.github.opletter.chesspg.components.widgets.ChessController
import io.github.opletter.chesspg.components.widgets.Chessboard
import io.github.opletter.chesspg.state.ClientState
import kotlinx.browser.window
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Text
import kotlin.time.Duration.Companion.seconds

@Composable
fun Menu(state: ClientState.Menu, lookForGame: () -> Unit, watchGame: (id: String, fen: String?) -> Unit) {
    Column(Modifier.gap(1.cssRem), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { lookForGame() }) {
            Text("Look for game")
        }
        if (state.activeGames.isNotEmpty())
            SpanText("Watch an ongoing game")
        Row(Modifier.gap(1.cssRem).overflowWrap(OverflowWrap.Normal)) {
            state.activeGames.forEach { (id, controller) ->
                Chessboard(
                    controller,
                    Modifier
                        .size(200.px)
                        .onClick { watchGame(id, controller.boardApi?.getFen()) }
                        .cursor(Cursor.Pointer)
                )
            }
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            val gameIds = window.api.get("activegames").decodeToString()
                .split(",").filter { it.isNotEmpty() }

            val newActiveGames = gameIds.associateWith { id ->
                state.activeGames[id] ?: run {
                    state.watch(id)
                    ChessController(ConfigBuilder {
                        viewOnly = true
                        coordinates = false
                        animation = AnimationBuilder { enabled = false } // initially
                    })
                }
            }

            // not needed in current state since all active games are watched
            state.activeGames.keys.subtract(newActiveGames.keys).forEach { id ->
                state.unwatch(id)
            }

            state.activeGames = newActiveGames

            delay(10.seconds)
        }
    }
}