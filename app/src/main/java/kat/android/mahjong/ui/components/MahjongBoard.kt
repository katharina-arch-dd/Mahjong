package kat.android.mahjong.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kat.android.mahjong.data.TableTheme
import kat.android.mahjong.logic.MahjongGameEngine
import kat.android.mahjong.model.GameState
import kat.android.mahjong.model.LayoutType
import kat.android.mahjong.model.MahjongTile

// Felt Green Table Colors
val TableGreenDark = Color(0xFF1B4323)
val TableGreenLight = Color(0xFF2E723C)
val TableWoodBorder = Color(0xFF4E2A10)

@Composable
fun MahjongBoard(
    gameState: GameState,
    tableTheme: TableTheme = TableTheme.GREEN,
    onTileClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeTiles = gameState.tiles.filter { !it.isMatched }

    val darkColor = Color(tableTheme.darkColorHex)
    val lightColor = Color(tableTheme.lightColorHex)

    // Wood frame & felt table background
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(TableWoodBorder)
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(lightColor, darkColor)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            val availableW = maxWidth.value
            val availableH = maxHeight.value

            // Grid bounds calculation based on layout
            val (maxGridX, maxGridY) = when (gameState.layout) {
                LayoutType.EASY -> Pair(10, 10)
                LayoutType.ARENA -> Pair(16, 12)
                LayoutType.CLASSIC -> Pair(30, 16)
            }

            // Calculate optimum tile width and height to fit screen
            val rawTileWidth = (availableW / (maxGridX / 2.0f + 1.2f)).coerceIn(28f, 54f)
            val rawTileHeight = (rawTileWidth * 1.32f).coerceIn(37f, 72f)

            val tileWidth = rawTileWidth.dp
            val tileHeight = rawTileHeight.dp

            val totalBoardW = (maxGridX / 2.0f) * rawTileWidth
            val totalBoardH = (maxGridY / 2.0f) * rawTileHeight

            // Sort tiles by z (bottom layer first), then y, then x for correct visual draw order
            val sortedTiles = activeTiles.sortedWith(
                compareBy<MahjongTile> { it.position.z }
                    .thenBy { it.position.y }
                    .thenBy { it.position.x }
            )

            Box(
                modifier = Modifier.size(totalBoardW.dp, totalBoardH.dp)
            ) {
                sortedTiles.forEach { tile ->
                    val isFree = MahjongGameEngine.isTileFree(tile, activeTiles)

                    // 3D layer offset
                    val layerXOffset = tile.position.z * 2.5f
                    val layerYOffset = tile.position.z * -2.5f

                    val pxX = (tile.position.x / 2.0f) * rawTileWidth + layerXOffset
                    val pxY = (tile.position.y / 2.0f) * rawTileHeight + layerYOffset

                    TileGraphic(
                        tile = tile,
                        tileWidth = tileWidth,
                        tileHeight = tileHeight,
                        isFree = isFree,
                        showFreeHighlight = gameState.highlightFreeTiles,
                        onClick = { onTileClick(tile.id) },
                        modifier = Modifier.offset(x = pxX.dp, y = pxY.dp)
                    )
                }
            }
        }
    }
}
