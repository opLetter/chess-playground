package io.github.opletter.chesspg.components.widgets

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text

@Composable
fun HeaderTitle() {
    H1 { Text("Chess Playground") }
}