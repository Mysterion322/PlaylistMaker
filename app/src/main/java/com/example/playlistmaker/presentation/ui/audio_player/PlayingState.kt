package com.example.playlistmaker.presentation.ui.audio_player

sealed interface PlayingState {
    object Default : PlayingState
    object Playing : PlayingState
    object Paused : PlayingState
    object Complete : PlayingState
}