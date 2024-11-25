package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.api.PlaylistRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val repository: PlaylistRepository) : PlaylistInteractor {

    override fun getPlaylists(): Flow<List<Playlist>> = repository.getPlaylists()
    override fun getPlaylistById(playlistId: Int): Flow<Playlist?> =
        repository.getPlaylistById(playlistId)

    override fun getAllTracks(playlistId: Int): Flow<List<Track>?> =
        repository.getAllTracks(playlistId)

    override fun createPlaylist(
        playlistName: String,
        playlistDescription: String,
        playlistImage: String?,
    ): Long {
        return repository.createPlaylist(playlistName, playlistDescription, playlistImage)
    }

    override fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override fun addToPlaylist(track: Track, playlistId: Int): Boolean {
        return repository.addToPlaylist(track, playlistId)
    }

    override suspend fun removeFromPlaylist(trackId: String, playlistId: Int) {
        repository.removeFromPlaylist(trackId, playlistId)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        repository.deletePlaylist(playlistId)
    }
}