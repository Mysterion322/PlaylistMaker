package com.example.playlistmaker.domain.api

import com.example.playlistmaker.presentation.ui.audio_player.PlayingState

interface AudioInteractor {

    fun pauseAudioPlayer()
    fun startAudioPlayer()
    fun releaseAudio()
    fun getCurrentAudioPosition(): Int
    fun getAudioState(): PlayingState

}