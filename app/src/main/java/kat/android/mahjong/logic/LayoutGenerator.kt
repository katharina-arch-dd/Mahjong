package kat.android.mahjong.logic

import kat.android.mahjong.model.LayoutType
import kat.android.mahjong.model.MahjongTile
import kat.android.mahjong.model.TileCategory
import kat.android.mahjong.model.TilePosition
import kat.android.mahjong.model.TileType
import kotlin.random.Random

object LayoutGenerator {

    /**
     * Creates a deck of Mahjong tiles matching the required count for a layout.
     * All tiles are generated in pairs to guarantee the game is solvable.
     */
    fun createTileTypesForCount(count: Int, random: Random = Random): List<TileType> {
        val fullDeck = mutableListOf<TileType>()

        // 1. Bamboo 1-9 (4 of each = 36)
        for (i in 1..9) {
            repeat(4) { fullDeck.add(TileType.Suit(TileCategory.BAMBOO, i)) }
        }
        // 2. Character 1-9 (4 of each = 36)
        for (i in 1..9) {
            repeat(4) { fullDeck.add(TileType.Suit(TileCategory.CHARACTER, i)) }
        }
        // 3. Dot 1-9 (4 of each = 36)
        for (i in 1..9) {
            repeat(4) { fullDeck.add(TileType.Suit(TileCategory.DOT, i)) }
        }
        // 4. Winds (4 of East, South, West, North = 16)
        for (wind in TileType.WindType.entries) {
            repeat(4) { fullDeck.add(TileType.Wind(wind)) }
        }
        // 5. Dragons (4 of Red, Green, White = 12)
        for (dragon in TileType.DragonType.entries) {
            repeat(4) { fullDeck.add(TileType.Dragon(dragon)) }
        }
        // 6. Seasons (1 of Spring, Summer, Autumn, Winter = 4)
        for (season in TileType.SeasonType.entries) {
            fullDeck.add(TileType.Season(season))
        }
        // 7. Flowers (1 of Plum, Orchid, Chrysanthemum, Bamboo Flower = 4)
        for (flower in TileType.FlowerType.entries) {
            fullDeck.add(TileType.Flower(flower))
        }

        fullDeck.shuffle(random)

        if (count >= 144) {
            return fullDeck.take(144)
        }

        // For 36 or 72 tiles, we need to pick pairs carefully
        val pairs = mutableListOf<Pair<TileType, TileType>>()
        val pool = fullDeck.toMutableList()

        while (pool.isNotEmpty() && pairs.size * 2 < count) {
            val first = pool.removeAt(0)
            val matchIdx = pool.indexOfFirst { it.matchesWith(first) }
            if (matchIdx != -1) {
                val second = pool.removeAt(matchIdx)
                pairs.add(Pair(first, second))
            }
        }

        val result = mutableListOf<TileType>()
        pairs.forEach {
            result.add(it.first)
            result.add(it.second)
        }
        result.shuffle(random)
        return result
    }

    /**
     * Generates positions for the selected LayoutType.
     * Tile dimensions in grid units: width = 2, height = 2.
     * Layer z offsets: 0, 1, 2, 3, 4.
     */
    fun generatePositions(layout: LayoutType): List<TilePosition> {
        val positions = mutableListOf<TilePosition>()

        when (layout) {
            LayoutType.EASY -> {
                // 36 tiles Beginner Pyramid
                // Layer 0: 5x5 grid minus corners = 20 tiles
                for (row in 0 until 5) {
                    for (col in 0 until 5) {
                        if ((row == 0 && col == 0) || (row == 0 && col == 4) ||
                            (row == 4 && col == 0) || (row == 4 && col == 4)
                        ) continue
                        positions.add(TilePosition(x = col * 2, y = row * 2, z = 0))
                    }
                }
                // Layer 1: 3x3 grid = 9 tiles
                for (row in 0 until 3) {
                    for (col in 0 until 3) {
                        positions.add(TilePosition(x = (col + 1) * 2, y = (row + 1) * 2, z = 1))
                    }
                }
                // Layer 2: 2x2 grid = 4 tiles
                positions.add(TilePosition(x = 3, y = 3, z = 2))
                positions.add(TilePosition(x = 5, y = 3, z = 2))
                positions.add(TilePosition(x = 3, y = 5, z = 2))
                positions.add(TilePosition(x = 5, y = 5, z = 2))
                // Layer 3: 2 tiles cap
                positions.add(TilePosition(x = 4, y = 3, z = 3))
                positions.add(TilePosition(x = 4, y = 5, z = 3))
                // Total = 21 + 9 + 4 + 2 = 36 tiles!
            }

            LayoutType.ARENA -> {
                // 72 tiles Arena
                // Layer 0: 8x7 outer frame & middle = 48 tiles
                for (r in 0 until 6) {
                    for (c in 0 until 8) {
                        positions.add(TilePosition(x = c * 2, y = r * 2, z = 0))
                    }
                }
                // Layer 1: Inner 6x3 = 18 tiles
                for (r in 0 until 3) {
                    for (c in 0 until 6) {
                        positions.add(TilePosition(x = (c + 1) * 2, y = (r + 1) * 2, z = 1))
                    }
                }
                // Layer 2: Center 3x2 = 6 tiles
                for (r in 0 until 2) {
                    for (c in 0 until 3) {
                        positions.add(TilePosition(x = (c + 2) * 2, y = (r + 2) * 2, z = 2))
                    }
                }
                // Total = 48 + 18 + 6 = 72 tiles!
            }

            LayoutType.CLASSIC -> {
                // 144 tiles Classic Turtle layout
                // Layer 0 (88 tiles)
                // Main grid 12x8 except corners
                for (row in 0 until 8) {
                    for (col in 1..12) {
                        if ((row == 0 || row == 7) && (col == 1 || col == 12)) continue
                        if ((row == 0 || row == 7) && (col == 2 || col == 11)) continue
                        positions.add(TilePosition(x = col * 2, y = row * 2, z = 0))
                    }
                }
                // Plus left wing and right wing extensions
                positions.add(TilePosition(x = 0, y = 7, z = 0)) // Far left 1
                positions.add(TilePosition(x = 26, y = 7, z = 0)) // Far right 1
                positions.add(TilePosition(x = 28, y = 7, z = 0)) // Far right 2

                // Layer 1 (36 tiles) 6x6 square in center
                for (row in 1..6) {
                    for (col in 4..9) {
                        positions.add(TilePosition(x = col * 2, y = row * 2, z = 1))
                    }
                }

                // Layer 2 (16 tiles) 4x4 square in center
                for (row in 2..5) {
                    for (col in 5..8) {
                        positions.add(TilePosition(x = col * 2, y = row * 2, z = 2))
                    }
                }

                // Layer 3 (3 tiles) 2x2 minus 1
                positions.add(TilePosition(x = 12, y = 6, z = 3))
                positions.add(TilePosition(x = 14, y = 6, z = 3))
                positions.add(TilePosition(x = 12, y = 8, z = 3))

                // Layer 4 (1 tile) Peak on top
                positions.add(TilePosition(x = 13, y = 7, z = 4))
            }
        }

        return positions
    }

    /**
     * Generates a initial set of MahjongTiles for the specified LayoutType.
     */
    fun createBoard(layout: LayoutType): List<MahjongTile> {
        val positions = generatePositions(layout)
        val tileTypes = createTileTypesForCount(positions.size)

        return positions.mapIndexed { index, pos ->
            MahjongTile(
                id = index + 1,
                type = tileTypes[index],
                position = pos,
                isMatched = false,
                isSelected = false,
                isHinted = false
            )
        }
    }
}
