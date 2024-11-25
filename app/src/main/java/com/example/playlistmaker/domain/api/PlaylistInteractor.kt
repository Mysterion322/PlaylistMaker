package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    fun getPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(playlistId: Int): Flow<Playlist?>
    fun getAllTracks(playlistId: Int): Flow<List<Track>?>
    fun createPlaylist(
        playlistName: String,
        playlistDescription: String,
        playlistImage: String?,
    ): Long
    fun updatePlaylist(playlist: Playlist)
    fun addToPlaylist(track: Track, playlistId: Int): Boolean
    suspend fun removeFromPlaylist(trackId: String, playlistId: Int)
    suspend fun deletePlaylist(playlistId: Int)
}