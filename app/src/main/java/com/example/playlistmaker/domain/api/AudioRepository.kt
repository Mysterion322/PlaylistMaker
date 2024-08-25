package com.example.playlistmaker.domain.api

import com.example.playlistmaker.presentation.ui.audio_player.PlayingState

interface AudioRepository {
    fun pausePlayer()
    fun startPlayer()
    fun release()
    fun getCurrentPosition(): Int
    fun getState(): PlayingState
}