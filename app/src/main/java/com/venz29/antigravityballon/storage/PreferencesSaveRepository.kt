package com.venz29.antigravityballon.storage

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.venz29.antigravityballon.core.models.SkinCatalog
import com.venz29.antigravityballon.core.storage.SaveData

class PreferencesSaveRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("antigravity_ballon_prefs", Context.MODE_PRIVATE)

    fun load(): SaveData {
        return try {
            val totalCoins = prefs.getInt("coins", 0)
            val bestScore = prefs.getInt("best_score", 0)
            val unlockedSkins = prefs.getStringSet("unlocked_skins", null)
                ?: setOf(SkinCatalog.DEFAULT_SKIN.id)
            val equippedSkin = prefs.getString("equipped_skin", SkinCatalog.DEFAULT_SKIN.id)
                ?: SkinCatalog.DEFAULT_SKIN.id
            val music = prefs.getBoolean("music_enabled", true)
            val sfx = prefs.getBoolean("sfx_enabled", true)
            val sensitivity = prefs.getBoolean("high_sensitivity", false)
            val doubleCoinsOwned = prefs.getBoolean("double_coins_owned", false)
            val shieldOwned = prefs.getBoolean("shield_owned", false)
            val runsPlayed = prefs.getInt("runs_played", 0)

            SaveData(
                totalCoins = totalCoins,
                bestScore = bestScore,
                unlockedSkinIds = unlockedSkins,
                equippedSkinId = equippedSkin,
                musicEnabled = music,
                sfxEnabled = sfx,
                highSensitivity = sensitivity,
                doubleCoinsOwned = doubleCoinsOwned,
                shieldOwned = shieldOwned,
                runsPlayed = runsPlayed
            ).validated()
        } catch (e: Exception) {
            Log.e("SaveRepo", "Corrupted save data detected, restoring defaults", e)
            SaveData().validated()
        }
    }

    fun save(data: SaveData) {
        try {
            val valid = data.validated()
            prefs.edit()
                .putInt("coins", valid.totalCoins)
                .putInt("best_score", valid.bestScore)
                .putStringSet("unlocked_skins", valid.unlockedSkinIds)
                .putString("equipped_skin", valid.equippedSkinId)
                .putBoolean("music_enabled", valid.musicEnabled)
                .putBoolean("sfx_enabled", valid.sfxEnabled)
                .putBoolean("high_sensitivity", valid.highSensitivity)
                .putBoolean("double_coins_owned", valid.doubleCoinsOwned)
                .putBoolean("shield_owned", valid.shieldOwned)
                .putInt("runs_played", valid.runsPlayed)
                .apply()
        } catch (e: Exception) {
            Log.e("SaveRepo", "Failed to save data", e)
        }
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
