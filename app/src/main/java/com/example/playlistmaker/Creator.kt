package com.example.playlistmaker

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.AudioRepository
import com.example.playlistmaker.data.repository.IsDarkThemeRepositoryImpl
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.domain.api.AudioInteractor
import com.example.playlistmaker.domain.api.IsDarkThemeInteractor
import com.example.playlistmaker.domain.api.IsDarkThemeRepository
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.IsDarkThemeInteractorImpl
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.TrackInteractorImpl


object Creator {

    private const val SP_PLAYLIST = "playlist_preferences"

    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }


    private fun provideSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(provideSharedPreferences())
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(provideSearchHistoryRepository())
    }

    fun provideAudioInteractor(url: String?): AudioInteractor {
        return AudioRepository((url))
    }

    fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(SP_PLAYLIST, MODE_PRIVATE)
    }

    private fun provideIsDarkThemeRepository(): IsDarkThemeRepository {
        return IsDarkThemeRepositoryImpl(provideSharedPreferences())
    }

    fun provideIsDarkThemeInteractor(): IsDarkThemeInteractor {
        return IsDarkThemeInteractorImpl(provideIsDarkThemeRepository())
    }


}