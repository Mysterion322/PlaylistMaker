package com.example.playlistmaker.presentation.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.IsDarkThemeInteractor

class SettingsViewModel (private val isDarkThemeInteractor: IsDarkThemeInteractor) : ViewModel() {

    private val isNightThemeValue = MutableLiveData(isDarkThemeInteractor.getIsNightTheme())
    fun updateThemeState(isNightTheme: Boolean) {
        isDarkThemeInteractor.saveTheme(isNightTheme)
        isNightThemeValue.value = isNightTheme
    }

    fun observeThemeState(): LiveData<Boolean> = isNightThemeValue

}