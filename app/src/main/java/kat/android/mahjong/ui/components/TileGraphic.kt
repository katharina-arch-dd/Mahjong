package kat.android.mahjong.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kat.android.mahjong.model.MahjongTile
import kat.android.mahjong.model.TileCategory
import kat.android.mahjong.model.TileType

// Color Palette for Mahjong Tiles
val TileIvoryFace = Color(0xFFFAF8F0)
val TileIvoryGradientEnd = Color(0xFFEDE8D5)
val TileResinGreenBack = Color(0xFF1B5E20)
val TileResinDarkEdge = Color(0xFF0D3311)
val TileSelectedGlow = Color(0xFFFFB300)
val TileHintGlow = Color(0xFF00E676)
val TileFreeBorder = Color(0xFF81C784)

val ColorRedSymbol = Color(0xFFD32F2F)
val ColorGreenSymbol = Color(0xFF2E7D32)
val ColorBlueSymbol = Color(0xFF1565C0)
val ColorDarkText = Color(0xFF212121)

@Composable
fun TileGraphic(
    tile: MahjongTile,
    tileWidth: Dp,
    tileHeight: Dp,
    isFree: Boolean,
    showFreeHighlight: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (tile.isMatched) return

    val zLevel = tile.position.z
    val depthOffsetPx = (zLevel * 2 + 3).dp

    // Animation for Hint pulsing
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by if (tile.isHinted) {
        infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )
    } else if (tile.isSelected) {
        infiniteTransition.animateFloat(
            initialValue = 1.03f,
            targetValue = 1.06f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )
    } else {
        remember { mutableFloatStateOf(1.0f) }
    }

    val elevation = (zLevel * 4 + 4).dp

    Box(
        modifier = modifier
            .size(tileWidth, tileHeight)
            .scale(pulseScale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        // 1. Tile 3D Depth Shadow & Resin Backing Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val sideDepth = depthOffsetPx.toPx()
            val r = 8.dp.toPx()

            // 3D Resin Green Backing
            drawRoundRect(
                color = TileResinDarkEdge,
                topLeft = Offset(sideDepth, sideDepth),
                size = Size(w - sideDepth, h - sideDepth),
                cornerRadius = CornerRadius(r, r)
            )

            drawRoundRect(
                color = TileResinGreenBack,
                topLeft = Offset(sideDepth * 0.7f, sideDepth * 0.7f),
                size = Size(w - sideDepth * 0.7f, h - sideDepth * 0.7f),
                cornerRadius = CornerRadius(r, r)
            )
        }

        // 2. Tile Front Ivory Face Surface
        Surface(
            modifier = Modifier
                .size(tileWidth - 4.dp, tileHeight - 5.dp)
                .shadow(
                    elevation = if (tile.isSelected) 12.dp else elevation,
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp)),
            color = TileIvoryFace
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Background subtle ivory gradient
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(TileIvoryFace, TileIvoryGradientEnd)
                        ),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                    )

                    // Draw Selection / Hint / Free Highlight Outline
                    when {
                        tile.isSelected -> {
                            drawRoundRect(
                                color = TileSelectedGlow,
                                style = Stroke(width = 4.dp.toPx()),
                                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                            )
                        }
                        tile.isHinted -> {
                            drawRoundRect(
                                color = TileHintGlow,
                                style = Stroke(width = 4.dp.toPx()),
                                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                            )
                        }
                        showFreeHighlight && isFree -> {
                            drawRoundRect(
                                color = TileFreeBorder,
                                style = Stroke(width = 2.dp.toPx()),
                                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                            )
                        }
                    }
                }

                // Tile Symbol Content
                TileSymbolContent(tileType = tile.type)

                // Beginner Corner Badge (shows easy label like "B3", "Z5", "Ost")
                Text(
                    text = tile.type.label,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 3.dp, top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun TileSymbolContent(tileType: TileType) {
    when (tileType) {
        is TileType.Suit -> {
            when (tileType.category) {
                TileCategory.BAMBOO -> BambooGraphic(number = tileType.number)
                TileCategory.CHARACTER -> CharacterGraphic(number = tileType.number)
                TileCategory.DOT -> DotGraphic(number = tileType.number)
                else -> {}
            }
        }

        is TileType.Wind -> WindGraphic(wind = tileType.wind)
        is TileType.Dragon -> DragonGraphic(dragon = tileType.dragon)
        is TileType.Season -> SeasonGraphic(season = tileType.season)
        is TileType.Flower -> FlowerGraphic(flower = tileType.flower)
    }
}

// ------------------- DRAWING GRAPHICS -------------------

@Composable
fun BambooGraphic(number: Int) {
    Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
        val w = size.width
        val h = size.height

        if (number == 1) {
            // 1-Bamboo: Large Sparrow / Peacock green motif
            drawCircle(color = ColorGreenSymbol, radius = w * 0.32f, center = Offset(w / 2, h / 2))
            drawCircle(color = ColorRedSymbol, radius = w * 0.15f, center = Offset(w / 2, h / 2))
            drawCircle(color = TileIvoryFace, radius = w * 0.08f, center = Offset(w / 2, h / 2))
        } else {
            // Draw bamboo sticks in grid
            val cols = if (number in listOf(2, 3)) 1 else if (number in listOf(4, 5, 6)) 2 else 3
            val rows = (number + cols - 1) / cols

            val dx = w / (cols + 1)
            val dy = h / (rows + 1)

            var count = 0
            for (r in 1..rows) {
                for (c in 1..cols) {
                    if (count >= number) break
                    val cx = dx * c
                    val cy = dy * r
                    val stickHeight = h / (rows * 2f)
                    val color = if (count % 2 == 0) ColorGreenSymbol else ColorRedSymbol

                    // Draw bamboo stick
                    drawLine(
                        color = color,
                        start = Offset(cx, cy - stickHeight / 2),
                        end = Offset(cx, cy + stickHeight / 2),
                        strokeWidth = 4.dp.toPx()
                    )
                    // Bamboo joint tick
                    drawCircle(color = ColorDarkText, radius = 2.dp.toPx(), center = Offset(cx, cy))
                    count++
                }
            }
        }
    }
}

@Composable
fun CharacterGraphic(number: Int) {
    val chineseNum = when (number) {
        1 -> "一"
        2 -> "二"
        3 -> "三"
        4 -> "四"
        5 -> "五"
        6 -> "六"
        7 -> "七"
        8 -> "八"
        9 -> "九"
        else -> "$number"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = chineseNum,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ColorBlueSymbol,
                lineHeight = 18.sp
            )
            Text(
                text = "萬",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = ColorRedSymbol,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
fun DotGraphic(number: Int) {
    Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
        val w = size.width
        val h = size.height

        if (number == 1) {
            // Large 1-Dot mandala
            drawCircle(color = ColorRedSymbol, radius = w * 0.35f, center = Offset(w / 2, h / 2))
            drawCircle(color = ColorBlueSymbol, radius = w * 0.22f, center = Offset(w / 2, h / 2))
            drawCircle(color = ColorGreenSymbol, radius = w * 0.10f, center = Offset(w / 2, h / 2))
        } else {
            val cols = if (number in 2..3) 1 else if (number in 4..6) 2 else 3
            val rows = (number + cols - 1) / cols

            val dx = w / (cols + 1)
            val dy = h / (rows + 1)

            val colors = listOf(ColorRedSymbol, ColorGreenSymbol, ColorBlueSymbol)

            var count = 0
            for (r in 1..rows) {
                for (c in 1..cols) {
                    if (count >= number) break
                    val cx = dx * c
                    val cy = dy * r
                    val color = colors[count % colors.size]

                    drawCircle(color = color, radius = (w / (cols * 3.5f)).coerceAtLeast(4.dp.toPx()), center = Offset(cx, cy))
                    count++
                }
            }
        }
    }
}

@Composable
fun WindGraphic(wind: TileType.WindType) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = wind.chinese,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = ColorDarkText
            )
            Text(
                text = wind.german,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorBlueSymbol
            )
        }
    }
}

@Composable
fun DragonGraphic(dragon: TileType.DragonType) {
    val (charText, color) = when (dragon) {
        TileType.DragonType.RED -> Pair("中", ColorRedSymbol)
        TileType.DragonType.GREEN -> Pair("發", ColorGreenSymbol)
        TileType.DragonType.WHITE -> Pair("白", ColorBlueSymbol)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (dragon == TileType.DragonType.WHITE) {
            // White Dragon is drawn as a blue rectangular border frame
            Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                drawRoundRect(
                    color = ColorBlueSymbol,
                    style = Stroke(width = 3.dp.toPx()),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
        } else {
            Text(
                text = charText,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
        }
    }
}

@Composable
fun SeasonGraphic(season: TileType.SeasonType) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = season.symbol,
                fontSize = 18.sp
            )
            Text(
                text = season.german,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = ColorRedSymbol,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FlowerGraphic(flower: TileType.FlowerType) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = flower.symbol,
                fontSize = 18.sp
            )
            Text(
                text = flower.german,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = ColorGreenSymbol,
                textAlign = TextAlign.Center
            )
        }
    }
}
