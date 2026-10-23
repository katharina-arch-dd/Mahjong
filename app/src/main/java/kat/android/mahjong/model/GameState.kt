package kat.android.mahjong.model

enum class LayoutType(val displayName: String, val tileCount: Int, val description: String) {
    EASY("Anfänger (Pyramide)", 36, "Perfekt zum Lernen & schnelle Spiele (36 Steine)"),
    ARENA("Mittel (Arena)", 72, "Ausgewogene Herausforderung (72 Steine)"),
    CLASSIC("Klassisch (Schildkröte)", 144, "Das traditionelle Mahjong-Erlebnis (144 Steine)")
}

enum class GameStatus {
    IDLE,
    PLAYING,
    WON,
    NO_MOVES
}

data class MatchedPair(
    val tile1: MahjongTile,
    val tile2: MahjongTile
)

data class GameState(
    val layout: LayoutType = LayoutType.EASY,
    val tiles: List<MahjongTile> = emptyList(),
    val selectedTileId: Int? = null,
    val hintTileIds: List<Int> = emptyList(),
    val history: List<MatchedPair> = emptyList(),
    val score: Int = 0,
    val secondsElapsed: Int = 0,
    val status: GameStatus = GameStatus.IDLE,
    val highlightFreeTiles: Boolean = false,
    val availableMovesCount: Int = 0,
    val messageText: String? = null
)
