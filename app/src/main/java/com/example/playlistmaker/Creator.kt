package com.example.playlistmaker

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
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


    fun provideSearchHistoryRepository(): SearchHistoryRepositoryImpl {
        return SearchHistoryRepositoryImpl(provideSharedPreferences())
    }

    fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(SP_PLAYLIST, MODE_PRIVATE)
    }


}