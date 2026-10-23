package kat.android.mahjong.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kat.android.mahjong.data.GamePreferences
import kat.android.mahjong.model.LayoutType
import kat.android.mahjong.ui.components.TableGreenDark
import kat.android.mahjong.ui.components.TableGreenLight
import kat.android.mahjong.ui.components.TableWoodBorder

@Composable
fun HomeScreen(
    onStartGame: (LayoutType) -> Unit,
    onOpenRules: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val gamePrefs = remember { GamePreferences(context) }

    var selectedLayout by remember { mutableStateOf(LayoutType.EASY) }

    val totalPlayed = gamePrefs.getTotalPlayed()
    val totalWins = gamePrefs.getTotalWins()
    val bestEasyScore = gamePrefs.getBestScore(LayoutType.EASY)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(TableGreenLight, TableGreenDark)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Title Header
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(TableWoodBorder)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1B5E20))
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🀄 MAHJONG 🀄",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFD54F),
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Klassisches Solitär für Anfänger",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFA5D6A7)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Statistics Summary Card (if games played > 0)
            if (totalPlayed > 0) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20).copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "Statistik: $totalWins Siege ($totalPlayed Spiele)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (bestEasyScore > 0) {
                                Text(
                                    text = "Anfänger Rekord: $bestEasyScore Punkte",
                                    fontSize = 12.sp,
                                    color = Color(0xFFA5D6A7)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Layout Selection Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Wähle deine Schwierigkeit:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LayoutType.entries.forEach { layout ->
                        val isSelected = selectedLayout == layout
                        val borderColor = if (isSelected) Color(0xFF2E7D32) else Color(0xFFE0E0E0)
                        val bgColor = if (isSelected) Color(0xFFE8F5E9) else Color.White

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgColor)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedLayout = layout }
                                .padding(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (isSelected) Color(0xFF2E7D32) else Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = layout.displayName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFF1B5E20) else Color(0xFF212121)
                                    )
                                    Text(
                                        text = layout.description,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Button(
                onClick = { onStartGame(selectedLayout) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color(0xFF3E2723),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SPIEL STARTEN",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF3E2723)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onOpenRules,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = BorderStroke(1.5.dp, Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Regeln & Anleitung",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Beginner features list
            Text(
                text = "✨ Ideal für Anfänger: Kostenlose Tipps, Unbegrenztes Mischen & Schritt-für-Schritt Erklärung!",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
        }
    }
}
