package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.api.PlaylistRepository
import com.example.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository,
) : PlaylistInteractor {
    override fun getPlaylists(): Flow<List<Playlist>> = repository.getPlaylists()

    override fun createPlaylist(
        playlistName: String,
        playlistDescription: String,
        playlistImage: String?,
    ): Long {
        return repository.createPlaylist(playlistName, playlistDescription, playlistImage)
    }

    override fun addToPlaylist(trackId: String, playlistId: Int): Boolean {
        return repository.addToPlaylist(trackId, playlistId)
    }

    override fun removeFromPlaylist(trackId: String, playlistName: Int) {
        repository.removeFromPlaylist(trackId, playlistName)
    }
}