package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getTrackById(trackId: String): Flow<Track>
    fun getFavoriteTracks(): Flow<List<Track>>
    fun addToFavorite(track: Track)
    fun removeFromFavorite(trackId: String)
}