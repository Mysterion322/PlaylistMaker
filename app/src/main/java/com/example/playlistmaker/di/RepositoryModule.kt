package com.example.playlistmaker.di

import com.example.playlistmaker.data.repository.AudioRepositoryImpl
import com.example.playlistmaker.data.repository.IsDarkThemeRepositoryImpl
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.domain.api.AudioRepository
import com.example.playlistmaker.domain.api.IsDarkThemeRepository
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TrackRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<TrackRepository> {
        TrackRepositoryImpl(get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get())
    }

    single<IsDarkThemeRepository> {
        IsDarkThemeRepositoryImpl(get())
    }

    factory<AudioRepository> {
        AudioRepositoryImpl(get())
    }


}