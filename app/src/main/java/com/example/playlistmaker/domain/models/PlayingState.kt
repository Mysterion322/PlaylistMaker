package com.example.playlistmaker.domain.models

sealed interface PlayingState{
    object Default: PlayingState
    object Prepared: PlayingState
    object Playing: PlayingState
    object Paused: PlayingState
    object Complete: PlayingState
}