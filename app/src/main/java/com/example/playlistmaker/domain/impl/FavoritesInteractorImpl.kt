package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.FavoritesInteractor
import com.example.playlistmaker.domain.api.FavoritesRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val repository: FavoritesRepository,
) : FavoritesInteractor {
    override fun isFavorite(trackId: String): Boolean {
        return repository.getTrackById(trackId).let { true }
    }

    override fun getTrackById(trackId: String): Flow<Track?> = repository.getTrackById(trackId)

    override fun getFavoriteTracks(): Flow<List<Track>> = repository.getFavoriteTracks()

    override fun addToFavorite(track: Track) {
        repository.addToFavorite(track)
        track.isFavorite = true
    }

    override fun removeFromFavorite(track: Track) {
        repository.removeFromFavorite(track.trackId)
        track.isFavorite = false
    }
}