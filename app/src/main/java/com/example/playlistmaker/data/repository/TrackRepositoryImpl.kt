package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.ItunesResponse
import com.example.playlistmaker.data.dto.TrackRequest
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Resource
import com.example.playlistmaker.domain.models.Track

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TrackRequest(expression))
        return when (response.resultCode) {
            200 -> {
                Resource.Success((response as ItunesResponse).results.map {
                    Track(
                        trackName = it.trackName,
                        artistName = it.artistName,
                        trackTimeMillis = it.trackTimeMillis,
                        artworkUrl100 = it.artworkUrl100,
                        collectionName = it.collectionName,
                        primaryGenreName = it.primaryGenreName,
                        releaseDate = it.releaseDate,
                        country = it.country,
                        previewUrl = it.previewUrl
                    )
                })
            }

            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }

            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }
}
