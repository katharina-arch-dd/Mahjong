package kat.android.mahjong

import kat.android.mahjong.logic.LayoutGenerator
import kat.android.mahjong.logic.MahjongGameEngine
import kat.android.mahjong.model.LayoutType
import kat.android.mahjong.model.TileCategory
import kat.android.mahjong.model.TileType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MahjongEngineTest {

    @Test
    fun testEasyBoardTileCountAndPairs() {
        val board = LayoutGenerator.createBoard(LayoutType.EASY)
        assertEquals(36, board.size)

        // Verify every tile has a matching partner in the deck
        board.forEach { tile ->
            val matchCount = board.count { it.type.matchesWith(tile.type) }
            assertTrue("Tile $tile should have at least 1 match", matchCount >= 2)
        }
    }

    @Test
    fun testSeasonAndFlowerMatching() {
        val spring = TileType.Season(TileType.SeasonType.SPRING)
        val winter = TileType.Season(TileType.SeasonType.WINTER)
        assertTrue(spring.matchesWith(winter))

        val plum = TileType.Flower(TileType.FlowerType.PLUM)
        val rose = TileType.Flower(TileType.FlowerType.BAMBOO_FLOWER)
        assertTrue(plum.matchesWith(rose))

        val bamboo1 = TileType.Suit(TileCategory.BAMBOO, 1)
        val bamboo2 = TileType.Suit(TileCategory.BAMBOO, 2)
        assertFalse(bamboo1.matchesWith(bamboo2))
    }

    @Test
    fun testEngineStartGameAndSelection() {
        val initialState = MahjongGameEngine.startNewGame(LayoutType.EASY)
        assertEquals(36, initialState.tiles.size)
        assertTrue(initialState.availableMovesCount > 0)

        val moves = MahjongGameEngine.getAvailableMoves(initialState.tiles)
        assertTrue(moves.isNotEmpty())

        val (first, second) = moves.first()

        // Select first tile
        val state1 = MahjongGameEngine.selectTile(initialState, first.id)
        assertEquals(first.id, state1.selectedTileId)

        // Select second tile (matching pair)
        val state2 = MahjongGameEngine.selectTile(state1, second.id)
        assertEquals(null, state2.selectedTileId)
        assertEquals(1, state2.history.size)
        assertEquals(100, state2.score)
        assertEquals(34, state2.tiles.count { !it.isMatched })
    }

    @Test
    fun testUndoAndShuffle() {
        val initialState = MahjongGameEngine.startNewGame(LayoutType.EASY)
        val moves = MahjongGameEngine.getAvailableMoves(initialState.tiles)
        val (first, second) = moves.first()

        val state1 = MahjongGameEngine.selectTile(initialState, first.id)
        val state2 = MahjongGameEngine.selectTile(state1, second.id)

        // Undo move
        val undoneState = MahjongGameEngine.undoLastMove(state2)
        assertEquals(0, undoneState.history.size)
        assertEquals(0, undoneState.score)
        assertEquals(36, undoneState.tiles.count { !it.isMatched })

        // Shuffle remaining
        val shuffledState = MahjongGameEngine.shuffleRemaining(undoneState)
        assertEquals(36, shuffledState.tiles.size)
        assertTrue(shuffledState.availableMovesCount >= 0)
    }
}
