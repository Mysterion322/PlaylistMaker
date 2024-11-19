package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    fun getPlaylists(): Flow<List<Playlist>>
    fun createPlaylist(
        playlistName: String,
        playlistDescription: String,
        playlistImage: String?,
    ): Long
    fun addToPlaylist(trackId: String, playlistId: Int): Boolean
    fun removeFromPlaylist(trackId: String, playlistId: Int)
}