package io.github.opletter.chesspg.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxHeight
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.core.Page
import io.github.opletter.chesspg.components.sections.ChessGameScreen
import io.github.opletter.chesspg.components.sections.Menu
import io.github.opletter.chesspg.components.widgets.HeaderTitle
import io.github.opletter.chesspg.state.ChessVM
import io.github.opletter.chesspg.state.ClientState
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
                Column(Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
                    HeaderTitle()
                    H2 { Text("Looking for game...") }
                }
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