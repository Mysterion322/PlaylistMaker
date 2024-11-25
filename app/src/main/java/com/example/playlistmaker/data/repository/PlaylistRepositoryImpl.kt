package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.converter.PlaylistDBConverter
import com.example.playlistmaker.data.converter.TrackAtPlaylistDBConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.TrackAtPlaylistEntity
import com.example.playlistmaker.domain.api.PlaylistRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val database: AppDatabase,
    private val playlistDBConverter: PlaylistDBConverter,
    private val trackDBConverter: TrackAtPlaylistDBConverter,
) : PlaylistRepository {
    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = database.playlistDao().getAllPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override fun getPlaylistById(playlistId: Int): Flow<Playlist?> = flow {
        val playlist = database.playlistDao().getPlaylistById(playlistId)
        emit(playlistDBConverter.map(playlist))
    }

    override fun getAllTracks(playlistId: Int): Flow<List<Track>?> = flow {
        val jsonTracks = database.playlistDao().getAllTracksFromPlaylist(playlistId)
        if (jsonTracks.isNotEmpty()) {
            val tracksIDs = playlistDBConverter.createTracksFromJson(jsonTracks)
            val tracksInPlaylist = database.playlistDao().getTrackByIds(tracksIDs).reversed()
            emit(convertFromTrackEntity(tracksInPlaylist))
        }
    }


    override fun createPlaylist(
        playlistName: String,
        playlistDescription: String,
        playlistImage: String?,
    ): Long {
        return database.playlistDao().insertPlaylist(
            PlaylistEntity(
                name = playlistName,
                description = playlistDescription,
                imagePath = playlistImage,
                tracks = "",
                tracksCount = 0
            )
        )
    }

    override fun updatePlaylist(playlist: Playlist) {
        database.playlistDao().updatePlaylist(
            playlistDBConverter.map(playlist)
        )
    }

    override fun addToPlaylist(track: Track, playlistId: Int): Boolean {
        val playlist = database.playlistDao().getPlaylistById(playlistId)
        val jsonTracks = playlist.tracks
        var tracksIds = ArrayList<String>()
        if (jsonTracks.isNotEmpty())
            tracksIds = playlistDBConverter.createTracksFromJson(jsonTracks)


        val currentTrack = tracksIds.filter { _trackId -> _trackId == track.trackId }
        if (currentTrack.isEmpty()) {
            tracksIds.add(track.trackId)
            database.playlistDao().updatePlaylist(
                playlist.copy(
                    tracks = playlistDBConverter.createJsonFromTracks(tracksIds),
                    tracksCount = tracksIds.size
                )
            )
            database.playlistDao().insertTrack(trackDBConverter.map(track))
            return true
        }
        return false
    }

    override suspend fun removeFromPlaylist(trackId: String, playlistId: Int) {
        val playlist = database.playlistDao().getPlaylistById(playlistId)
        val jsonTracks = playlist.tracks
        if (jsonTracks.isNotEmpty()) {
            val tracks = playlistDBConverter.createTracksFromJson(jsonTracks)
            val currentTrack = tracks.filter { track -> track == trackId }
            if (currentTrack.isNotEmpty()) {
                tracks.remove(trackId)
                database.playlistDao().updatePlaylist(
                    playlist.copy(
                        tracks = playlistDBConverter.createJsonFromTracks(tracks),
                        tracksCount = tracks.size
                    )
                )
                checkTrackInPlaylists(trackId)
            }
        }
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        val playlist = database.playlistDao().getPlaylistById(playlistId)
        val jsonTracks = playlist.tracks
        database.playlistDao().deletePlaylist(playlistId)
        if (jsonTracks.isNotEmpty()) {
            playlistDBConverter.createTracksFromJson(jsonTracks).forEach { trackId ->
                checkTrackInPlaylists(trackId)
            }
        }
    }

    suspend fun checkTrackInPlaylists(trackID: String) {
        var inPlaylist = false
        getPlaylists().collect { playlist ->
            for (item in playlist)
                if (item.tracks.contains(trackID)) {
                    inPlaylist = true
                    break
                }
        }
        if (!inPlaylist)
            database.playlistDao().deleteTrack(trackID)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDBConverter.map(playlist) }
    }

    private fun convertFromTrackEntity(tracksEntity: List<TrackAtPlaylistEntity>): List<Track> {
        return tracksEntity.map { track -> trackDBConverter.map(track) }
    }
}