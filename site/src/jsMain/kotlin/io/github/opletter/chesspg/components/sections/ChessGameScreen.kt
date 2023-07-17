package io.github.opletter.chesspg.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.AlignItems
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.toAttrs
import io.github.opletter.chesspg.components.widgets.Chessboard
import io.github.opletter.chesspg.state.ClientState
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Text

val ChessGameScreenStyle by ComponentStyle {
    base {
        Modifier
            .display(DisplayStyle.Grid)
            .gridTemplateRows { repeat(3) { size(1.fr) } }
            .placeItems(AlignItems.Center, JustifyItems.Center)
            .padding(1.cssRem)
            .fillMaxSize()
    }
    Breakpoint.MD {
        Modifier
            .gridTemplateRows(GridTemplate.Unset)
            .gridTemplateColumns { size(1.fr); size(2.fr); size(1.fr) }
            .height(80.percent)
    }
}

@Composable
fun ChessGameScreen(header: String, curState: ClientState.ActiveGame, onBack: () -> Unit) {
    Div(ChessGameScreenStyle.toAttrs()) {
        Column(
            Modifier
                .fillMaxHeight()
                .padding(topBottom = 2.cssRem),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            H2 { Text(header) }
            curState.bannerMessage?.let {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(0.5.cssRem)
                        .backgroundColor(Colors.Black)
                        .color(Colors.White)
                        .fontSize(125.percent)
                        .fontWeight(FontWeight.Bold)
                        .textAlign(TextAlign.Center)
                        .borderRadius(6.px)
                ) { Text(it) }
                Spacer()
                Button(onClick = { onBack() }) {
                    Text("Back to menu")
                }
            }
        }
        Chessboard(
            curState.controller,
            Modifier
                .fillMaxHeight()
                .aspectRatio(1)
                .maxHeight(700.px)
        )
        Box()
    }
}