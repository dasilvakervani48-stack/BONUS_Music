package com.bonus.musicplayer

import android.content.Context

class AppPreferences(context: Context) {
    enum class ThemeMode {
        SYSTEM,
        LIGHT,
        DARK
    }
    private val prefs =
        context.getSharedPreferences(
            "BONUS_Music",
            Context.MODE_PRIVATE
        )

    var themeMode: ThemeMode
        get() = when (
            prefs.getString("theme_mode", "SYSTEM")
        ) {
            "LIGHT" -> ThemeMode.LIGHT
            "DARK" -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
        set(value) {
            prefs.edit()
                .putString("theme_mode", value.name)
                .apply()
        }

    var dolbyEnabled: Boolean
        get() = prefs.getBoolean("dolby", false)
        set(value) {
            prefs.edit()
                .putBoolean("dolby", value)
                .apply()
        }

    var spatialAudioEnabled: Boolean
        get() = prefs.getBoolean("spatial_audio", false)
        set(value) {
            prefs.edit()
                .putBoolean("spatial_audio", value)
                .apply()
        }

    var explicitContentEnabled: Boolean
        get() = prefs.getBoolean("explicit_content", false)
        set(value) {
            prefs.edit()
                .putBoolean("explicit_content", value)
                .apply()
        }

    var audioQuality: String
        get() = prefs.getString(
            "audio_quality",
            "Auto"
        ) ?: "Auto"
        set(value) {
            prefs.edit()
                .putString("audio_quality", value)
                .apply()
        }
}