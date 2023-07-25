package io.github.opletter.chesspg.components.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.browser.api
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.forms.Button
import io.github.opletter.chessground.AnimationBuilder
import io.github.opletter.chessground.ConfigBuilder
import io.github.opletter.chesspg.components.widgets.ChessController
import io.github.opletter.chesspg.components.widgets.Chessboard
import io.github.opletter.chesspg.components.widgets.HeaderTitle
import io.github.opletter.chesspg.state.ClientState
import kotlinx.browser.window
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Text
import kotlin.time.Duration.Companion.seconds

@Composable
fun Menu(state: ClientState.Menu, lookForGame: () -> Unit, watchGame: (id: String, fen: String?) -> Unit) {
    Column(
        Modifier
            .fillMaxHeight()
            .padding(topBottom = 0.5.cssRem, leftRight = 0.25.cssRem)
            .gap(1.cssRem),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderTitle()
        Button(onClick = { lookForGame() }) {
            Text("Look for game")
        }
        if (state.activeGames.isEmpty()) return@Column

        H3 { Text("Or select an ongoing game to watch :)") }
        Row(Modifier.gap(1.cssRem).flexWrap(FlexWrap.Wrap), horizontalArrangement = Arrangement.Center) {
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
                .split(",")
                .filter { it.isNotEmpty() }
                .associate { it.substringBefore(":") to it.substringAfter(":") }

            state.activeGames = gameIds.mapValues { (id, fen) ->
                state.activeGames[id] ?: ChessController(ConfigBuilder {
                    this.fen = fen
                    viewOnly = true
                    coordinates = false
                    animation = AnimationBuilder { enabled = false } // initially
                })
            }

            delay(10.seconds)
        }
    }
}