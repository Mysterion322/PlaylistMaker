package com.example.playlistmaker.domain.api

interface IsDarkThemeRepository {
    fun saveIsDarkTheme(isDarkTheme: Boolean)
    fun getIsDarkTheme(): Boolean
}