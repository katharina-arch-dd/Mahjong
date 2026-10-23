package kat.android.mahjong.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kat.android.mahjong.data.GamePreferences
import kat.android.mahjong.data.TableTheme
import kat.android.mahjong.logic.MahjongGameEngine
import kat.android.mahjong.model.GameState
import kat.android.mahjong.model.GameStatus
import kat.android.mahjong.model.LayoutType
import kat.android.mahjong.ui.components.MahjongBoard
import kat.android.mahjong.ui.components.TableGreenDark
import kat.android.mahjong.util.SoundEffects
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    layout: LayoutType,
    onBackToHome: () -> Unit,
    onOpenRules: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val gamePrefs = remember { GamePreferences(context) }

    var tableTheme by remember { mutableStateOf(gamePrefs.getTableTheme()) }
    var soundEnabled by remember { mutableStateOf(gamePrefs.isSoundEnabled()) }
    var showThemeDialog by remember { mutableStateOf(false) }

    var gameState by remember(layout) {
        mutableStateOf(MahjongGameEngine.startNewGame(layout))
    }

    // Increment games played counter when game starts
    LaunchedEffect(layout) {
        gamePrefs.incrementGamesPlayed()
    }

    // Game Timer in seconds
    LaunchedEffect(gameState.status) {
        if (gameState.status == GameStatus.PLAYING) {
            while (true) {
                delay(1000L)
                gameState = gameState.copy(secondsElapsed = gameState.secondsElapsed + 1)
            }
        } else if (gameState.status == GameStatus.WON) {
            if (soundEnabled) SoundEffects.playWin()
            gamePrefs.saveGameWin(layout, gameState.score, gameState.secondsElapsed)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = layout.displayName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Punkte: ${gameState.score}  |  ⏱️ ${formatTimer(gameState.secondsElapsed)}",
                            fontSize = 12.sp,
                            color = Color(0xFFFFD54F)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackToHome) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                },
                actions = {
                    // Theme Selector Icon
                    IconButton(onClick = { showThemeDialog = true }) {
                        Icon(
                            Icons.Default.Palette,
                            contentDescription = "Design ändern",
                            tint = Color.White
                        )
                    }

                    // Free Tiles Highlight Toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Freie",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Switch(
                            checked = gameState.highlightFreeTiles,
                            onCheckedChange = {
                                gameState = gameState.copy(highlightFreeTiles = it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFFFD54F),
                                checkedTrackColor = Color(0xFF2E7D32)
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TableGreenDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(tableTheme.darkColorHex))
        ) {
            // Helper Message Toast Bar
            AnimatedVisibility(
                visible = gameState.messageText != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                gameState.messageText?.let { msg ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF33691E)),
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = msg,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Main Board View
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                MahjongBoard(
                    gameState = gameState,
                    tableTheme = tableTheme,
                    onTileClick = { tileId ->
                        val oldHistorySize = gameState.history.size
                        val newState = MahjongGameEngine.selectTile(gameState, tileId)

                        if (soundEnabled) {
                            if (newState.history.size > oldHistorySize) {
                                SoundEffects.playMatch()
                            } else if (newState.messageText?.contains("blockiert") == true) {
                                SoundEffects.playError()
                            } else {
                                SoundEffects.playClick()
                            }
                        }
                        gameState = newState
                    }
                )
            }

            // Bottom Action Control Toolbar
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D3311)),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hint Button
                    ActionButton(
                        icon = Icons.Default.Lightbulb,
                        label = "Tipp",
                        badgeText = "${gameState.availableMovesCount}",
                        tint = Color(0xFFFFD54F),
                        onClick = {
                            if (soundEnabled) SoundEffects.playClick()
                            gameState = MahjongGameEngine.showHint(gameState)
                        }
                    )

                    // Undo Button
                    ActionButton(
                        icon = Icons.AutoMirrored.Filled.Undo,
                        label = "Rückgängig",
                        enabled = gameState.history.isNotEmpty(),
                        tint = Color(0xFF81D4FA),
                        onClick = {
                            if (soundEnabled) SoundEffects.playClick()
                            gameState = MahjongGameEngine.undoLastMove(gameState)
                        }
                    )

                    // Shuffle Button
                    ActionButton(
                        icon = Icons.Default.Shuffle,
                        label = "Mischen",
                        tint = Color(0xFFA5D6A7),
                        onClick = {
                            if (soundEnabled) SoundEffects.playClick()
                            gameState = MahjongGameEngine.shuffleRemaining(gameState)
                        }
                    )

                    // Rules Button
                    ActionButton(
                        icon = Icons.Default.HelpOutline,
                        label = "Regeln",
                        tint = Color(0xFFFFCC80),
                        onClick = onOpenRules
                    )

                    // Restart Button
                    ActionButton(
                        icon = Icons.Default.Refresh,
                        label = "Neu",
                        tint = Color(0xFFEF9A9A),
                        onClick = {
                            if (soundEnabled) SoundEffects.playClick()
                            gameState = MahjongGameEngine.startNewGame(layout)
                        }
                    )
                }
            }
        }

        // Theme Change Dialog
        if (showThemeDialog) {
            AlertDialog(
                onDismissRequest = { showThemeDialog = false },
                title = { Text("Tisch-Design wählen", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        TableTheme.entries.forEach { theme ->
                            TextButton(
                                onClick = {
                                    tableTheme = theme
                                    gamePrefs.setTableTheme(theme)
                                    showThemeDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = theme.displayName + if (theme == tableTheme) " ✓" else "",
                                    fontWeight = if (theme == tableTheme) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 16.sp,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showThemeDialog = false }) {
                        Text("Schließen")
                    }
                }
            )
        }

        // Victory Dialog
        if (gameState.status == GameStatus.WON) {
            val bestTime = gamePrefs.getBestTime(layout)
            val bestScore = gamePrefs.getBestScore(layout)

            AlertDialog(
                onDismissRequest = {},
                title = { Text("🎉 Gewonnen!", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Fantastisch! Du hast alle Steine abgeräumt.")
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("• Zeit: ${formatTimer(gameState.secondsElapsed)} (Bestzeit: ${formatTimer(bestTime)})")
                        Text("• Punkte: ${gameState.score} (Rekord: $bestScore)")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            gameState = MahjongGameEngine.startNewGame(layout)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("Noch einmal spielen")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onBackToHome) {
                        Text("Hauptmenü")
                    }
                }
            )
        }

        // No Moves Left Dialog
        if (gameState.status == GameStatus.NO_MOVES) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Keine Züge mehr möglich 🧩") },
                text = { Text("Möchtest du die verbleibenden Steine neu mischen oder deinen letzten Zug rückgängig machen?") },
                confirmButton = {
                    Button(
                        onClick = {
                            gameState = MahjongGameEngine.shuffleRemaining(gameState)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300))
                    ) {
                        Text("Steine mischen", color = Color.Black)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        gameState = MahjongGameEngine.undoLastMove(gameState)
                    }) {
                        Text("Zug rückgängig machen")
                    }
                }
            )
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    label: String,
    badgeText: String? = null,
    tint: Color = Color.White,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(52.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.TopEnd) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (enabled) tint else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = label,
                fontSize = 10.sp,
                color = if (enabled) Color.White else Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

fun formatTimer(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
}
