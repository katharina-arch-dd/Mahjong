package kat.android.mahjong.model

/**
 * Category/Suit of Mahjong Tile
 */
enum class TileCategory(val displayName: String) {
    BAMBOO("Bambus"),
    CHARACTER("Zahlen"),
    DOT("Kreise"),
    WIND("Winde"),
    DRAGON("Drachen"),
    SEASON("Jahreszeiten"),
    FLOWER("Blumen")
}

/**
 * Specific Tile Type definition
 */
sealed class TileType {
    abstract val category: TileCategory
    abstract val identifier: String
    abstract val label: String

    // Suit Tile (Bamboo 1-9, Character 1-9, Dot 1-9)
    data class Suit(
        override val category: TileCategory,
        val number: Int // 1 to 9
    ) : TileType() {
        override val identifier: String = "${category.name}_$number"
        override val label: String = when (category) {
            TileCategory.BAMBOO -> "B$number"
            TileCategory.CHARACTER -> "Z$number"
            TileCategory.DOT -> "K$number"
            else -> "$number"
        }
    }

    // Wind (East, South, West, North)
    enum class WindType(val chinese: String, val german: String, val shortLabel: String) {
        EAST("東", "Ost", "O"),
        SOUTH("南", "Süd", "S"),
        WEST("西", "West", "W"),
        NORTH("北", "Nord", "N")
    }

    data class Wind(val wind: WindType) : TileType() {
        override val category: TileCategory = TileCategory.WIND
        override val identifier: String = "WIND_${wind.name}"
        override val label: String = wind.shortLabel
    }

    // Dragon (Red, Green, White)
    enum class DragonType(val chinese: String, val german: String, val shortLabel: String) {
        RED("中", "Roter Drache", "Rot"),
        GREEN("發", "Grüner Drache", "Grün"),
        WHITE("白", "Weißer Drache", "Weiß")
    }

    data class Dragon(val dragon: DragonType) : TileType() {
        override val category: TileCategory = TileCategory.DRAGON
        override val identifier: String = "DRAGON_${dragon.name}"
        override val label: String = dragon.shortLabel
    }

    // Season (Spring, Summer, Autumn, Winter) - any Season matches any Season!
    enum class SeasonType(val german: String, val symbol: String) {
        SPRING("Frühling", "🌸"),
        SUMMER("Sommer", "☀️"),
        AUTUMN("Herbst", "🍁"),
        WINTER("Winter", "❄️")
    }

    data class Season(val season: SeasonType) : TileType() {
        override val category: TileCategory = TileCategory.SEASON
        override val identifier: String = "SEASON_${season.name}"
        override val label: String = season.german
    }

    // Flower (Plum, Orchid, Chrysanthemum, BambooFlower) - any Flower matches any Flower!
    enum class FlowerType(val german: String, val symbol: String) {
        PLUM("Pflaume", "🌺"),
        ORCHID("Orchidee", "🪻"),
        CHRYSANTHEMUM("Chrysantheme", "🌼"),
        BAMBOO_FLOWER("Rose", "🌹")
    }

    data class Flower(val flower: FlowerType) : TileType() {
        override val category: TileCategory = TileCategory.FLOWER
        override val identifier: String = "FLOWER_${flower.name}"
        override val label: String = flower.german
    }

    /**
     * Matching rules in Mahjong Solitaire:
     * - Seasons match with any Season
     * - Flowers match with any Flower
     * - All other tiles require exact same TileType
     */
    fun matchesWith(other: TileType): Boolean {
        if (this is Season && other is Season) return true
        if (this is Flower && other is Flower) return true
        return this == other
    }
}

/**
 * Position of a tile on the Mahjong board grid.
 * Grid coordinates (x, y) allow half-tile spacing (e.g. 0 to 30 for x, 0 to 20 for y).
 * z represents vertical layer level (0 is bottom table layer, 1, 2, 3, 4 higher layers).
 */
data class TilePosition(
    val x: Int, // grid x position
    val y: Int, // grid y position
    val z: Int  // layer z position
)

/**
 * Instance of a Tile placed on the board.
 */
data class MahjongTile(
    val id: Int,
    val type: TileType,
    val position: TilePosition,
    val isMatched: Boolean = false,
    val isSelected: Boolean = false,
    val isHinted: Boolean = false
)
