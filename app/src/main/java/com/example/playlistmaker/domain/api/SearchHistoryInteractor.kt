package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryInteractor {
    fun getHistory(): Flow<List<Track>>
    fun clearHistory()
    fun addToHistory(track: Track)
}