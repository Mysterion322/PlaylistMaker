package com.example.playlistmaker.data.converter

import com.example.playlistmaker.data.db.entity.TrackAtPlaylistEntity
import com.example.playlistmaker.domain.models.Track

class TrackAtPlaylistDBConverter {
    fun map(track: Track): TrackAtPlaylistEntity {
        return TrackAtPlaylistEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            previewUrl = track.previewUrl,
            collectionName = track.collectionName,
            primaryGenreName = track.primaryGenreName,
            releaseDate = track.releaseDate.orEmpty(),
            country = track.country,
            addedAt = System.currentTimeMillis().toString(),
            isFavorite = track.isFavorite

        )
    }

    fun map(trackEntity: TrackAtPlaylistEntity): Track {
        return Track(
            trackId = trackEntity.trackId,
            trackName = trackEntity.trackName,
            artistName = trackEntity.artistName,
            trackTimeMillis = trackEntity.trackTimeMillis,
            artworkUrl100 = trackEntity.artworkUrl100,
            previewUrl = trackEntity.previewUrl,
            collectionName = trackEntity.collectionName,
            primaryGenreName = trackEntity.primaryGenreName,
            releaseDate = trackEntity.releaseDate,
            country = trackEntity.country,
            isFavorite = trackEntity.isFavorite,
            addedTime = trackEntity.addedAt.toLong()
        )
    }
}