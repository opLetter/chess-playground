package io.github.opletter.chesspg.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.AlignItems
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.JustifyItems
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.forms.Button
import io.github.opletter.chesspg.components.widgets.Chessboard
import io.github.opletter.chesspg.state.ClientState
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Text

@Composable
fun ChessGameScreen(header: String, curState: ClientState.ActiveGame, onBack: () -> Unit) {
    Div(
        Modifier
            .display(DisplayStyle.Grid)
            .gridTemplateColumns { size(1.fr); size(2.fr); size(1.fr) }
            .placeItems(AlignItems.Center, JustifyItems.Center)
            .padding(1.cssRem)
            .fillMaxSize()
            .toAttrs()
    ) {
        Column(Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
            H2 { Text(header) }
            curState.bannerMessage?.let {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(0.5.cssRem)
                        .backgroundColor(Colors.DeepSkyBlue)
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
                .height(90.percent)
                .aspectRatio(1)
                .maxHeight(700.px)
        )
        Box()
    }
}