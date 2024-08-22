package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.PlayingState

interface AudioInteractor {
    var state: PlayingState
    fun pausePlayer()
    fun preparePlayer()
    fun startPlayer()
    fun release()
    fun getCurrentPosition(): Int
}