package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun getTracks(): Flow<List<Track>>
    fun addTrack(newTrack: Track)
    fun clearHistory()
}