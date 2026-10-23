package kat.android.mahjong

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kat.android.mahjong.model.LayoutType
import kat.android.mahjong.ui.screens.GameScreen
import kat.android.mahjong.ui.screens.HomeScreen
import kat.android.mahjong.ui.screens.RulesScreen
import kat.android.mahjong.ui.theme.MahjongTheme

sealed class Screen {
    data object Home : Screen()
    data class Game(val layout: LayoutType) : Screen()
    data object Rules : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MahjongTheme {
                MahjongApp()
            }
        }
    }
}

@Composable
fun MahjongApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (val screen = currentScreen) {
            is Screen.Home -> {
                HomeScreen(
                    onStartGame = { layout ->
                        currentScreen = Screen.Game(layout)
                    },
                    onOpenRules = {
                        currentScreen = Screen.Rules
                    },
                    modifier = modifier
                )
            }

            is Screen.Game -> {
                GameScreen(
                    layout = screen.layout,
                    onBackToHome = {
                        currentScreen = Screen.Home
                    },
                    onOpenRules = {
                        currentScreen = Screen.Rules
                    },
                    modifier = modifier
                )
            }

            is Screen.Rules -> {
                RulesScreen(
                    onBackClick = {
                        currentScreen = Screen.Home
                    },
                    modifier = modifier
                )
            }
        }
    }
}
