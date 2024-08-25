package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.THEME_KEY
import com.example.playlistmaker.domain.api.IsDarkThemeRepository

class IsDarkThemeRepositoryImpl (private val sharedPref: SharedPreferences): IsDarkThemeRepository {
    private var darkTheme = false

    override fun saveIsDarkTheme(isDarkTheme: Boolean){
        darkTheme=isDarkTheme
        sharedPref.edit().putBoolean(THEME_KEY, darkTheme).apply()
    }

    override fun getIsDarkTheme(): Boolean{
        return sharedPref.getBoolean(THEME_KEY, false)
    }



}