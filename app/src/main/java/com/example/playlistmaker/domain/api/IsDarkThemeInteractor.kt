package com.example.playlistmaker.domain.api

interface IsDarkThemeInteractor {
    fun saveTheme(isDarkTheme: Boolean)
    fun getIsNightTheme(): Boolean
}