package com.example.playlistmaker.di

import com.example.playlistmaker.domain.api.AudioInteractor
import com.example.playlistmaker.domain.api.FavoritesInteractor
import com.example.playlistmaker.domain.api.IsDarkThemeInteractor
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.impl.AudioInteractorImpl
import com.example.playlistmaker.domain.impl.FavoritesInteractorImpl
import com.example.playlistmaker.domain.impl.IsDarkThemeInteractorImpl
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.TrackInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<TrackInteractor> {
        TrackInteractorImpl(get())
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    single<IsDarkThemeInteractor> {
        IsDarkThemeInteractorImpl(get())
    }

    factory<AudioInteractor> {
        AudioInteractorImpl(get())
    }

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }

}