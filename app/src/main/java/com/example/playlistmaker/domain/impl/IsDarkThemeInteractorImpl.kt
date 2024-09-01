package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.IsDarkThemeInteractor
import com.example.playlistmaker.domain.api.IsDarkThemeRepository

class IsDarkThemeInteractorImpl(private val isDarkThemeRepository: IsDarkThemeRepository): IsDarkThemeInteractor {
    override fun saveTheme(isDarkTheme: Boolean){
        isDarkThemeRepository.saveIsDarkTheme(isDarkTheme)
    }

    override fun getIsNightTheme(): Boolean{
        return isDarkThemeRepository.getIsDarkTheme()
    }
}