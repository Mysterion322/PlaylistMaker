package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun getTracks(): MutableList<Track>
    fun addTrack(newTrack: Track)
    fun clearHistory()
}