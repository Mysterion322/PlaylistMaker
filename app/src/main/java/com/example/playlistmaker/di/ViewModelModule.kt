package com.example.playlistmaker.di

import com.example.playlistmaker.domain.api.AudioInteractor
import com.example.playlistmaker.presentation.view_models.AudioPlayerViewModel
import com.example.playlistmaker.presentation.view_models.SearchViewModel
import com.example.playlistmaker.presentation.view_models.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val viewModelModule = module {

    viewModel{
        SettingsViewModel(get())
    }

    viewModel{
        SearchViewModel(get(), get(), get())
    }

    viewModel { (url: String) ->
        AudioPlayerViewModel(
            trackPlayerInteractor = get<AudioInteractor>(parameters = { parametersOf(url) }),
        )
    }


}