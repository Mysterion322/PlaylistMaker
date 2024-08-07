package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun getHistory(): MutableList<Track>
    fun clearHistory()
    fun addToHistory(track: Track)
}