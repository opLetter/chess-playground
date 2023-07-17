package io.github.opletter.chesspg.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.varabyte.kobweb.compose.css.CSSBackground
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.compose.css.functions.toImage
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.base
import io.github.opletter.chesspg.components.sections.ChessGameScreen
import io.github.opletter.chesspg.components.sections.Menu
import io.github.opletter.chesspg.components.widgets.HeaderTitle
import io.github.opletter.chesspg.state.ChessVM
import io.github.opletter.chesspg.state.ClientState
import org.jetbrains.compose.web.css.deg
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Text

@Page
@Composable
fun HomePage() {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = remember { ChessVM(coroutineScope) }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val curState = viewModel.state) {
            is ClientState.Menu -> {
                Menu(
                    curState,
                    lookForGame = { viewModel.lookForGame() },
                    watchGame = { id, fen -> viewModel.watchGame(id, fen) },
                )
            }

            is ClientState.LookingForGame -> {
                HeaderTitle()
                H2 { Text("Looking for game...") }
            }

            is ClientState.Playing -> {
                ChessGameScreen(
                    "Playing: ${curState.playerColor}",
                    curState,
                    onBack = { viewModel.backToMenu() },
                )
            }

            is ClientState.Watching -> {
                ChessGameScreen("Watching", curState, onBack = { viewModel.backToMenu() })
            }
        }
    }
}

val x by ComponentStyle.base {
    Modifier
        .background(
            CSSBackground(
                image = linearGradient(
                    90.deg,
                    Color.hsl(314.deg, 0.percent, 31.percent),
                    Color.hsl(314.deg, 0.percent, 31.percent)
                ).toImage()
            ),
            CSSBackground(
                image = linearGradient(329.deg) {
                    add(Color.rgba(35, 35, 35, 0.02f), 0.percent)
                    add(Color.rgba(35, 35, 35, 0.02f), 20.percent)
                    add(Color.rgba(246, 246, 246, 0.02f), 20.percent)
                    add(Color.rgba(246, 246, 246, 0.02f), 40.percent)
                    add(Color.rgba(118, 118, 118, 0.02f), 40.percent)
                    add(Color.rgba(118, 118, 118, 0.02f), 60.percent)
                    add(Color.rgba(245, 245, 245, 0.02f), 60.percent)
                    add(Color.rgba(245, 245, 245, 0.02f), 80.percent)
                    add(Color.rgba(140, 140, 140, 0.02f), 80.percent)
                    add(Color.rgba(140, 140, 140, 0.02f), 100.percent)
                }.toImage()
            ),
            CSSBackground(
                image = linearGradient(109.deg) {
                    add(Color.rgba(124, 124, 124, 0.03f), 0.percent)
                    add(Color.rgba(124, 124, 124, 0.03f), 12.5.percent)
                    add(Color.rgba(61, 61, 61, 0.03f), 12.5.percent)
                    add(Color.rgba(61, 61, 61, 0.03f), 25.percent)
                    add(Color.rgba(187, 187, 187, 0.03f), 25.percent)
                    add(Color.rgba(187, 187, 187, 0.03f), 37.5.percent)
                    add(Color.rgba(207, 207, 207, 0.03f), 37.5.percent)
                    add(Color.rgba(207, 207, 207, 0.03f), 50.percent)
                    add(Color.rgba(206, 206, 206, 0.03f), 50.percent)
                    add(Color.rgba(206, 206, 206, 0.03f), 62.5.percent)
                    add(Color.rgba(118, 118, 118, 0.03f), 62.5.percent)
                    add(Color.rgba(118, 118, 118, 0.03f), 75.percent)
                    add(Color.rgba(89, 89, 89, 0.03f), 75.percent)
                    add(Color.rgba(89, 89, 89, 0.03f), 87.5.percent)
                    add(Color.rgba(96, 96, 96, 0.03f), 87.5.percent)
                    add(Color.rgba(96, 96, 96, 0.03f), 100.percent)
                }.toImage()
            ),
            CSSBackground(
                image = linearGradient(279.deg) {
                    add(Color.rgba(190, 190, 190, 0.02f), 0.percent)
                    add(Color.rgba(190, 190, 190, 0.02f), 14.286.percent)
                    add(Color.rgba(160, 160, 160, 0.02f), 14.286.percent)
                    add(Color.rgba(160, 160, 160, 0.02f), 28.572.percent)
                    add(Color.rgba(23, 23, 23, 0.02f), 28.572.percent)
                    add(Color.rgba(23, 23, 23, 0.02f), 42.858.percent)
                    add(Color.rgba(60, 60, 60, 0.02f), 42.858.percent)
                    add(Color.rgba(60, 60, 60, 0.02f), 57.144.percent)
                    add(Color.rgba(149, 149, 149, 0.02f), 57.144.percent)
                    add(Color.rgba(149, 149, 149, 0.02f), 71.42999999999999.percent)
                    add(Color.rgba(4, 4, 4, 0.02f), 71.43.percent)
                    add(Color.rgba(4, 4, 4, 0.02f), 85.71600000000001.percent)
                    add(Color.rgba(50, 50, 50, 0.02f), 85.716.percent)
                    add(Color.rgba(50, 50, 50, 0.02f), 100.002.percent)
                }.toImage()
            ),
            CSSBackground(
                image = linearGradient(6.deg) {
                    add(Color.rgba(31, 31, 31, 0.02f), 0.percent)
                    add(Color.rgba(31, 31, 31, 0.02f), 20.percent)
                    add(Color.rgba(193, 193, 193, 0.02f), 20.percent)
                    add(Color.rgba(193, 193, 193, 0.02f), 40.percent)
                    add(Color.rgba(139, 139, 139, 0.02f), 40.percent)
                    add(Color.rgba(139, 139, 139, 0.02f), 60.percent)
                    add(Color.rgba(14, 14, 14, 0.02f), 60.percent)
                    add(Color.rgba(14, 14, 14, 0.02f), 80.percent)
                    add(Color.rgba(122, 122, 122, 0.02f), 80.percent)
                    add(Color.rgba(122, 122, 122, 0.02f), 100.percent)
                }.toImage()
            ),
            CSSBackground(
                image = linearGradient(300.deg) {
                    add(Color.rgba(243, 243, 243, 0.01f), 0.percent)
                    add(Color.rgba(243, 243, 243, 0.01f), 12.5.percent)
                    add(Color.rgba(209, 209, 209, 0.01f), 12.5.percent)
                    add(Color.rgba(209, 209, 209, 0.01f), 25.percent)
                    add(Color.rgba(179, 179, 179, 0.01f), 25.percent)
                    add(Color.rgba(179, 179, 179, 0.01f), 37.5.percent)
                    add(Color.rgba(3, 3, 3, 0.01f), 37.5.percent)
                    add(Color.rgba(3, 3, 3, 0.01f), 50.percent)
                    add(Color.rgba(211, 211, 211, 0.01f), 50.percent)
                    add(Color.rgba(211, 211, 211, 0.01f), 62.5.percent)
                    add(Color.rgba(151, 151, 151, 0.01f), 62.5.percent)
                    add(Color.rgba(151, 151, 151, 0.01f), 75.percent)
                    add(Color.rgba(16, 16, 16, 0.01f), 75.percent)
                    add(Color.rgba(16, 16, 16, 0.01f), 87.5.percent)
                    add(Color.rgba(242, 242, 242, 0.01f), 87.5.percent)
                    add(Color.rgba(242, 242, 242, 0.01f), 100.percent)
                }.toImage()
            ),
            CSSBackground(
                image = linearGradient(314.deg) {
                    add(Color.rgba(187, 187, 187, 0.02f), 0.percent)
                    add(Color.rgba(187, 187, 187, 0.02f), 12.5.percent)
                    add(Color.rgba(170, 170, 170, 0.02f), 12.5.percent)
                    add(Color.rgba(170, 170, 170, 0.02f), 25.percent)
                    add(Color.rgba(214, 214, 214, 0.02f), 25.percent)
                    add(Color.rgba(214, 214, 214, 0.02f), 37.5.percent)
                    add(Color.rgba(187, 187, 187, 0.02f), 37.5.percent)
                    add(Color.rgba(187, 187, 187, 0.02f), 50.percent)
                    add(Color.rgba(190, 190, 190, 0.02f), 50.percent)
                    add(Color.rgba(190, 190, 190, 0.02f), 62.5.percent)
                    add(Color.rgba(6, 6, 6, 0.02f), 62.5.percent)
                    add(Color.rgba(6, 6, 6, 0.02f), 75.percent)
                    add(Color.rgba(206, 206, 206, 0.02f), 75.percent)
                    add(Color.rgba(206, 206, 206, 0.02f), 87.5.percent)
                    add(Color.rgba(171, 171, 171, 0.02f), 87.5.percent)
                    add(Color.rgba(171, 171, 171, 0.02f), 100.percent)
                }.toImage()
            ),
            CSSBackground(
                image = linearGradient(337.deg) {
                    add(Color.rgba(142, 142, 142, 0.02f), 0.percent)
                    add(Color.rgba(142, 142, 142, 0.02f), 20.percent)
                    add(Color.rgba(164, 164, 164, 0.02f), 20.percent)
                    add(Color.rgba(164, 164, 164, 0.02f), 40.percent)
                    add(Color.rgba(203, 203, 203, 0.02f), 40.percent)
                    add(Color.rgba(203, 203, 203, 0.02f), 60.percent)
                    add(Color.rgba(228, 228, 228, 0.02f), 60.percent)
                    add(Color.rgba(228, 228, 228, 0.02f), 80.percent)
                    add(Color.rgba(54, 54, 54, 0.02f), 80.percent)
                    add(Color.rgba(54, 54, 54, 0.02f), 100.percent)
                }.toImage()
            ),
            CSSBackground(
                image = linearGradient(45.deg) {
                    add(Color.rgba(223, 223, 223, 0.02f), 0.percent)
                    add(Color.rgba(223, 223, 223, 0.02f), 14.286.percent)
                    add(Color.rgba(70, 70, 70, 0.02f), 14.286.percent)
                    add(Color.rgba(70, 70, 70, 0.02f), 28.572.percent)
                    add(Color.rgba(109, 109, 109, 0.02f), 28.572.percent)
                    add(Color.rgba(109, 109, 109, 0.02f), 42.858.percent)
                    add(Color.rgba(19, 19, 19, 0.02f), 42.858.percent)
                    add(Color.rgba(19, 19, 19, 0.02f), 57.144.percent)
                    add(Color.rgba(180, 180, 180, 0.02f), 57.144.percent)
                    add(Color.rgba(180, 180, 180, 0.02f), 71.42999999999999.percent)
                    add(Color.rgba(63, 63, 63, 0.02f), 71.43.percent)
                    add(Color.rgba(63, 63, 63, 0.02f), 85.71600000000001.percent)
                    add(Color.rgba(87, 87, 87, 0.02f), 85.716.percent)
                    add(Color.rgba(87, 87, 87, 0.02f), 100.002.percent)
                }.toImage()
            ),
            CSSBackground(
                image = linearGradient(161.deg) {
                    add(Color.rgba(121, 121, 121, 0.02f), 0.percent)
                    add(Color.rgba(121, 121, 121, 0.02f), 16.667.percent)
                    add(Color.rgba(193, 193, 193, 0.02f), 16.667.percent)
                    add(Color.rgba(193, 193, 193, 0.02f), 33.334.percent)
                    add(Color.rgba(177, 177, 177, 0.02f), 33.334.percent)
                    add(Color.rgba(177, 177, 177, 0.02f), 50.001000000000005.percent)
                    add(Color.rgba(5, 5, 5, 0.02f), 50.001.percent)
                    add(Color.rgba(5, 5, 5, 0.02f), 66.668.percent)
                    add(Color.rgba(229, 229, 229, 0.02f), 66.668.percent)
                    add(Color.rgba(229, 229, 229, 0.02f), 83.33500000000001.percent)
                    add(Color.rgba(211, 211, 211, 0.02f), 83.335.percent)
                    add(Color.rgba(211, 211, 211, 0.02f), 100.002.percent)
                }.toImage()
            )
        )
}