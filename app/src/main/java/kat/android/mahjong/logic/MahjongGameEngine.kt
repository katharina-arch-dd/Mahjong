package kat.android.mahjong.logic

import kat.android.mahjong.model.GameState
import kat.android.mahjong.model.GameStatus
import kat.android.mahjong.model.LayoutType
import kat.android.mahjong.model.MahjongTile
import kat.android.mahjong.model.MatchedPair
import kotlin.math.abs

object MahjongGameEngine {

    /**
     * Checks if a specific tile is free to be selected.
     */
    fun isTileFree(tile: MahjongTile, activeTiles: List<MahjongTile>): Boolean {
        if (tile.isMatched) return false

        val active = activeTiles.filter { !it.isMatched && it.id != tile.id }

        // 1. Check if covered from top
        val coveredFromTop = active.any { other ->
            other.position.z > tile.position.z &&
                    abs(other.position.x - tile.position.x) < 2 &&
                    abs(other.position.y - tile.position.y) < 2
        }
        if (coveredFromTop) return false

        // 2. Check if blocked on left
        val blockedLeft = active.any { other ->
            other.position.z == tile.position.z &&
                    tile.position.x - other.position.x in 1..2 &&
                    abs(other.position.y - tile.position.y) < 2
        }

        // 3. Check if blocked on right
        val blockedRight = active.any { other ->
            other.position.z == tile.position.z &&
                    other.position.x - tile.position.x in 1..2 &&
                    abs(other.position.y - tile.position.y) < 2
        }

        return !(blockedLeft && blockedRight)
    }

    /**
     * Finds all available pairs of matching free tiles.
     */
    fun getAvailableMoves(tiles: List<MahjongTile>): List<Pair<MahjongTile, MahjongTile>> {
        val activeTiles = tiles.filter { !it.isMatched }
        val freeTiles = activeTiles.filter { isTileFree(it, activeTiles) }

        val moves = mutableListOf<Pair<MahjongTile, MahjongTile>>()
        for (i in freeTiles.indices) {
            for (j in i + 1 until freeTiles.size) {
                if (freeTiles[i].type.matchesWith(freeTiles[j].type)) {
                    moves.add(Pair(freeTiles[i], freeTiles[j]))
                }
            }
        }
        return moves
    }

    /**
     * Starts a new game with the given layout.
     */
    fun startNewGame(layout: LayoutType): GameState {
        val tiles = LayoutGenerator.createBoard(layout)
        val availableMoves = getAvailableMoves(tiles)
        return GameState(
            layout = layout,
            tiles = tiles,
            selectedTileId = null,
            hintTileIds = emptyList(),
            history = emptyList(),
            score = 0,
            secondsElapsed = 0,
            status = GameStatus.PLAYING,
            availableMovesCount = availableMoves.size,
            messageText = "Finde zwei gleiche freie Steine!"
        )
    }

    /**
     * Handles clicking on a tile.
     */
    fun selectTile(currentState: GameState, tileId: Int): GameState {
        if (currentState.status != GameStatus.PLAYING) return currentState

        val tile = currentState.tiles.find { it.id == tileId } ?: return currentState
        if (tile.isMatched) return currentState

        val activeTiles = currentState.tiles.filter { !it.isMatched }

        // If tile is not free, show friendly beginner explanation
        if (!isTileFree(tile, activeTiles)) {
            val isCovered = activeTiles.any {
                it.position.z > tile.position.z &&
                        abs(it.position.x - tile.position.x) < 2 &&
                        abs(it.position.y - tile.position.y) < 2
            }
            val explanation = if (isCovered) {
                "Stein ist verdeckt! Entferne zuerst die Steine darüber."
            } else {
                "Stein ist blockiert! Er muss links oder rechts frei sein."
            }
            return currentState.copy(messageText = explanation)
        }

        val currentlySelectedId = currentState.selectedTileId

        if (currentlySelectedId == null) {
            // Select this tile
            val newTiles = currentState.tiles.map {
                if (it.id == tileId) it.copy(isSelected = true)
                else it.copy(isSelected = false, isHinted = false)
            }
            return currentState.copy(
                tiles = newTiles,
                selectedTileId = tileId,
                hintTileIds = emptyList(),
                messageText = "Stein ausgewählt. Finde den passenden Partner!"
            )
        } else if (currentlySelectedId == tileId) {
            // Deselect tile
            val newTiles = currentState.tiles.map { it.copy(isSelected = false) }
            return currentState.copy(
                tiles = newTiles,
                selectedTileId = null,
                messageText = null
            )
        } else {
            // Second tile clicked! Check match
            val selectedTile = currentState.tiles.find { it.id == currentlySelectedId }
            if (selectedTile != null && selectedTile.type.matchesWith(tile.type)) {
                // MATCH SUCCESSFUL!
                val newTiles = currentState.tiles.map {
                    if (it.id == currentlySelectedId || it.id == tileId) {
                        it.copy(isMatched = true, isSelected = false, isHinted = false)
                    } else {
                        it.copy(isHinted = false)
                    }
                }

                val matchedPair = MatchedPair(selectedTile, tile)
                val newHistory = currentState.history + matchedPair
                val newScore = currentState.score + 100

                // Check Win or No Moves left
                val remainingTiles = newTiles.filter { !it.isMatched }
                val newAvailableMoves = getAvailableMoves(newTiles)

                val newStatus = when {
                    remainingTiles.isEmpty() -> GameStatus.WON
                    newAvailableMoves.isEmpty() -> GameStatus.NO_MOVES
                    else -> GameStatus.PLAYING
                }

                val msg = when (newStatus) {
                    GameStatus.WON -> "Herzlichen Glückwunsch! Du hast gewonnen! 🎉"
                    GameStatus.NO_MOVES -> "Keine Züge mehr möglich! Nutze 'Mischen' oder 'Rückgängig'."
                    else -> "Perfekt! Paar gefunden (+100 Punkte)."
                }

                return currentState.copy(
                    tiles = newTiles,
                    selectedTileId = null,
                    hintTileIds = emptyList(),
                    history = newHistory,
                    score = newScore,
                    status = newStatus,
                    availableMovesCount = newAvailableMoves.size,
                    messageText = msg
                )
            } else {
                // Not a match: select the new free tile instead
                val newTiles = currentState.tiles.map {
                    if (it.id == tileId) it.copy(isSelected = true)
                    else it.copy(isSelected = false, isHinted = false)
                }
                return currentState.copy(
                    tiles = newTiles,
                    selectedTileId = tileId,
                    hintTileIds = emptyList(),
                    messageText = "Steine passen nicht zusammen. Neuer Stein gewählt."
                )
            }
        }
    }

    /**
     * Highlights a valid move for the user.
     */
    fun showHint(currentState: GameState): GameState {
        if (currentState.status != GameStatus.PLAYING) return currentState

        val moves = getAvailableMoves(currentState.tiles)
        if (moves.isEmpty()) {
            return currentState.copy(messageText = "Keine passenden freien Züge mehr verfügbar!")
        }

        val move = moves.random()
        val hintIds = listOf(move.first.id, move.second.id)

        val newTiles = currentState.tiles.map {
            if (it.id in hintIds) it.copy(isHinted = true)
            else it.copy(isHinted = false)
        }

        return currentState.copy(
            tiles = newTiles,
            hintTileIds = hintIds,
            messageText = "Tipp: Die zwei hervorgehobenen Steine passen zusammen! 💡"
        )
    }

    /**
     * Reshuffles remaining unmatched tiles into existing tile positions.
     */
    fun shuffleRemaining(currentState: GameState): GameState {
        val activeTiles = currentState.tiles.filter { !it.isMatched }
        if (activeTiles.isEmpty()) return currentState

        val activeTypes = activeTiles.map { it.type }.shuffled()

        var typeIdx = 0
        val newTiles = currentState.tiles.map { tile ->
            if (!tile.isMatched) {
                tile.copy(
                    type = activeTypes[typeIdx++],
                    isSelected = false,
                    isHinted = false
                )
            } else tile
        }

        val availableMoves = getAvailableMoves(newTiles)
        val newStatus = if (availableMoves.isEmpty()) GameStatus.NO_MOVES else GameStatus.PLAYING

        return currentState.copy(
            tiles = newTiles,
            selectedTileId = null,
            hintTileIds = emptyList(),
            status = newStatus,
            availableMovesCount = availableMoves.size,
            messageText = "Steine neu gemischt! (${availableMoves.size} Züge verfügbar)"
        )
    }

    /**
     * Reverts the last matched pair.
     */
    fun undoLastMove(currentState: GameState): GameState {
        if (currentState.history.isEmpty()) return currentState

        val lastPair = currentState.history.last()
        val newHistory = currentState.history.dropLast(1)

        val restoredIds = setOf(lastPair.tile1.id, lastPair.tile2.id)

        val newTiles = currentState.tiles.map { tile ->
            if (tile.id in restoredIds) {
                tile.copy(isMatched = false, isSelected = false, isHinted = false)
            } else tile.copy(isSelected = false, isHinted = false)
        }

        val availableMoves = getAvailableMoves(newTiles)
        val newScore = (currentState.score - 100).coerceAtLeast(0)

        return currentState.copy(
            tiles = newTiles,
            selectedTileId = null,
            hintTileIds = emptyList(),
            history = newHistory,
            score = newScore,
            status = GameStatus.PLAYING,
            availableMovesCount = availableMoves.size,
            messageText = "Zug rückgängig gemacht."
        )
    }
}
