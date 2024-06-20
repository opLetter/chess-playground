package io.github.opletter.chesspg

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Background
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.functions.LinearGradient
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.compose.css.functions.toImage
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.graphics.lightened
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.forms.ButtonStyle
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.modifyStyle
import org.jetbrains.compose.web.css.deg
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.vh

@InitSilk
fun updateTheme(ctx: InitSilkContext) {
    ctx.config.initialColorMode = ColorMode.DARK
    ctx.theme.palettes.dark.apply {
        background = Color.rgba(105, 105, 105, 0.5f)
        color = Colors.Black
    }
    ctx.theme.modifyStyle(ButtonStyle) {
        base {
            Modifier
                .fontWeight(FontWeight.Bold)
                .color(Color.rgb(0x909090).lightened())
                .background(
                    Background.of(
                        image = linearGradient(LinearGradient.Direction.ToBottom) {
                            add(Color.hsl(37.deg, 7.percent, 22.percent))
                            add(Color.hsl(37.deg, 5.percent, 19.percent), 100.percent)
                        }.toImage()
                    )
                )
        }
        hover {
            Modifier
                .color(Color.rgb(0xc0c0c0).lightened())
                .background(
                    Background.of(
                        image = linearGradient(LinearGradient.Direction.ToBottom) {
                            add(Color.hsl(37.deg, 7.percent, 25.percent))
                            add(Color.hsl(37.deg, 5.percent, 22.percent), 100.percent)
                        }.toImage()
                    )
                )
        }
    }
}

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
    SilkApp {
        Surface(SmoothColorStyle.toModifier().minHeight(100.vh)) {
            content()
        }
    }
}