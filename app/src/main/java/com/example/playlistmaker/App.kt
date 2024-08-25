package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

val THEME_KEY = "key_for_theme"

class App: Application() {

    var darkTheme = false
        private set

    override fun onCreate() {
        super.onCreate()

        Creator.initApplication(this)
        switchTheme(Creator.provideIsDarkThemeInteractor().getIsNightTheme())
    }
    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

}