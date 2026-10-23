package kat.android.mahjong.data

import android.content.Context
import android.content.SharedPreferences
import kat.android.mahjong.model.LayoutType

enum class TableTheme(val displayName: String, val darkColorHex: Long, val lightColorHex: Long) {
    GREEN("Klassisch Grün", 0xFF1B4323, 0xFF2E723C),
    WOOD("Elegantes Holz", 0xFF3E2723, 0xFF5D4037),
    BLUE("Königliches Blau", 0xFF0D47A1, 0xFF1976D2)
}

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("mahjong_prefs", Context.MODE_PRIVATE)

    fun getBestScore(layout: LayoutType): Int {
        return prefs.getInt("best_score_${layout.name}", 0)
    }

    fun getBestTime(layout: LayoutType): Int {
        return prefs.getInt("best_time_${layout.name}", 0)
    }

    fun saveGameWin(layout: LayoutType, score: Int, timeSeconds: Int) {
        val currentBestScore = getBestScore(layout)
        if (score > currentBestScore) {
            prefs.edit().putInt("best_score_${layout.name}", score).apply()
        }

        val currentBestTime = getBestTime(layout)
        if (currentBestTime == 0 || timeSeconds < currentBestTime) {
            prefs.edit().putInt("best_time_${layout.name}", timeSeconds).apply()
        }

        val totalWins = prefs.getInt("total_wins", 0) + 1
        prefs.edit().putInt("total_wins", totalWins).apply()
    }

    fun incrementGamesPlayed() {
        val total = prefs.getInt("total_played", 0) + 1
        prefs.edit().putInt("total_played", total).apply()
    }

    fun getTotalPlayed(): Int = prefs.getInt("total_played", 0)
    fun getTotalWins(): Int = prefs.getInt("total_wins", 0)

    fun getTableTheme(): TableTheme {
        val name = prefs.getString("table_theme", TableTheme.GREEN.name)
        return try {
            TableTheme.valueOf(name ?: TableTheme.GREEN.name)
        } catch (_: Exception) {
            TableTheme.GREEN
        }
    }

    fun setTableTheme(theme: TableTheme) {
        prefs.edit().putString("table_theme", theme.name).apply()
    }

    fun isSoundEnabled(): Boolean = prefs.getBoolean("sound_enabled", true)
    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("sound_enabled", enabled).apply()
    }
}
