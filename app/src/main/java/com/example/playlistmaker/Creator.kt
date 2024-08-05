package com.example.playlistmaker

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.AudioRepositoryImpl
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.domain.api.AudioInteractor
import com.example.playlistmaker.domain.api.AudioRepository
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.AudioInteractorImpl
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.domain.models.Track


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


    fun provideSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(provideSharedPreferences())
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(provideSearchHistoryRepository())
    }

    fun provideAudioRepository(mediaPlayer: MediaPlayer, playIV: ImageView, handler: Handler,
                               trackTimerTV: TextView, track: Track?
    ): AudioRepository {
        return AudioRepositoryImpl(mediaPlayer, playIV, handler, trackTimerTV, track)
    }

    fun provideAudioInteractor(mediaPlayer: MediaPlayer, playIV: ImageView, handler: Handler,
                               trackTimerTV: TextView, track: Track?
    ): AudioInteractor {
        return AudioInteractorImpl(provideAudioRepository(mediaPlayer, playIV, handler, trackTimerTV, track))
    }

    private fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(SP_PLAYLIST, MODE_PRIVATE)
    }


}