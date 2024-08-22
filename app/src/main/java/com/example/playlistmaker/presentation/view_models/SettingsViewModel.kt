package com.example.playlistmaker.presentation.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.Creator

class SettingsViewModel () : ViewModel() {

    private val IsDarkThemeInteractor by lazy { Creator.provideIsDarkThemeInteractor() }

    private val isNightThemeValue = MutableLiveData(IsDarkThemeInteractor.getIsNightTheme())
    fun updateThemeState(isNightTheme: Boolean) {
        IsDarkThemeInteractor.saveTheme(isNightTheme)
        isNightThemeValue.value = isNightTheme
    }

    fun observeThemeState(): LiveData<Boolean> = isNightThemeValue

}