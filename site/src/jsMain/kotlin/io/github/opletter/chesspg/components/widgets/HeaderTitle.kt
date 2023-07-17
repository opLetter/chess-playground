package io.github.opletter.chesspg.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.AlignSelf
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.alignSelf
import com.varabyte.kobweb.compose.ui.toAttrs
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text

@Composable
fun HeaderTitle() {
    H1(Modifier.alignSelf(AlignSelf.Start).toAttrs()) { Text("Chess Playground") }
}