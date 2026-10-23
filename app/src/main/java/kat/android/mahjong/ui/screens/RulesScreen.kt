package kat.android.mahjong.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kat.android.mahjong.ui.components.TableGreenDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mahjong Grundregeln & Anleitung", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
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
                .background(Color(0xFFF5F3ED))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Intro Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Willkommen bei Mahjong Solitär!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = "Das Ziel ist einfach: Entferne alle Steine vom Spielfeld, indem du zwei identische, freie Steine miteinander kombinierst.",
                            fontSize = 14.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            // Rule 1: Wann ist ein Stein frei?
            RuleSectionCard(
                title = "1. Wann ist ein Stein frei?",
                icon = Icons.Default.CheckCircle,
                iconColor = Color(0xFF1976D2)
            ) {
                Text(
                    text = "Ein Mahjong-Stein kann nur gewählt werden, wenn er FREI liegt. Das bedeutet:",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                BulletPoint("1. Kein anderer Stein liegt OBEN DARAUF.")
                BulletPoint("2. Der Stein hat LINKS oder RECHTS eine freie Seite (er ist nicht von beiden Seiten eingeklemmt).")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "💡 Tipp für Anfänger: Aktiviere im Spiel den Schalter 'Freie Steine', um sofort zu sehen, welche Steine du anklicken kannst!",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2E7D32)
                )
            }

            // Rule 2: Steine-Kategorien
            RuleSectionCard(
                title = "2. Welche Steine passen zusammen?",
                icon = Icons.Default.Star,
                iconColor = Color(0xFFF57C00)
            ) {
                Text(
                    text = "In der Regel passen zwei Steine zusammen, wenn sie das GLEICHE MOTIV zeigen:",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                BulletPoint("🀐 Bambus (1 bis 9): Zeigen grüne & rote Bambusstäbe.")
                BulletPoint("🀇 Zahlen (1 bis 9): Zeigen chinesische Zahlen 1-9.")
                BulletPoint("🀙 Kreise (1 bis 9): Zeigen farbige Münz-Kreise.")
                BulletPoint("🀀 Winde (Ost, Süd, West, Nord): Genau gleiche Winde passen zusammen.")
                BulletPoint("🀄 Drachen (Rot, Grün, Weiß): Genau gleiche Drachen passen zusammen.")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "✨ SONDERREGELN (Besonders einfach):",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F)
                )
                BulletPoint("🌸 Jahreszeiten (Frühling, Sommer, Herbst, Winter): Jede Jahreszeit passt zu JEDER anderen Jahreszeit!")
                BulletPoint("🌺 Blumen (Pflaume, Orchidee, Chrysantheme, Rose): Jede Blume passt zu JEDER anderen Blume!")
            }

            // Rule 3: Hilfen für Anfänger
            RuleSectionCard(
                title = "3. Hilfen für Anfänger",
                icon = Icons.Default.Lightbulb,
                iconColor = Color(0xFFFBC02D)
            ) {
                BulletPoint("💡 Tipp-Knopf: Zeigt dir sofort ein passendes freies Paar auf dem Feld an.")
                BulletPoint("🔀 Mischen: Mischt alle verbleibenden Steine neu durch, falls keine Züge mehr möglich sind.")
                BulletPoint("↩️ Rückgängig: Macht deinen letzten Zug rückgängig.")
                BulletPoint("🏷️ Eck-Bezeichnungen: Jedes Motiv hat oben links ein einfaches Kürzel (z.B. 'B3' = Bambus 3, 'Ost' = Ostwind).")
            }

            // Rule 4: Strategie-Tipps
            RuleSectionCard(
                title = "4. Tipps für den Erfolg",
                icon = Icons.Default.Refresh,
                iconColor = Color(0xFF7B1FA2)
            ) {
                BulletPoint("1. Hohe Stapel zuerst abbauen: Steine auf oberen Ebenen verfälschen die Sicht und blockieren viele Steine darunter.")
                BulletPoint("2. Lange Reihen auflösen: Steine in langen Reihen blockieren die Mitte.")
                BulletPoint("3. Vorausschauen: Überlege vor jedem Zug, welche Steine durch dein Paar freigelegt werden.")
            }
        }
    }
}

@Composable
fun RuleSectionCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp)
    ) {
        Text("• ", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
        Text(text = text, fontSize = 13.sp, color = Color(0xFF424242))
    }
}
