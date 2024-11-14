package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.dto.ItunesResponse
import com.example.playlistmaker.data.dto.TrackRequest
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Resource
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(private val networkClient: NetworkClient,
                          private val appDB: AppDatabase,) : TrackRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackRequest(expression))
        when (response.resultCode) {
            200 -> {
                with(response as ItunesResponse) {
                    val trackList = response.results.map {

                        Track(
                            trackId = it.trackId,
                            trackName = it.trackName,
                            artistName = it.artistName,
                            trackTimeMillis = it.trackTimeMillis,
                            artworkUrl100 = it.artworkUrl100,
                            collectionName = it.collectionName,
                            primaryGenreName = it.primaryGenreName,
                            releaseDate = it.releaseDate,
                            country = it.country,
                            previewUrl = it.previewUrl,
                            isFavorite = appDB.trackDao().isFavorite(it.trackId)
                        )
                    }
                    emit(Resource.Success(trackList))
                }
            }

            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }

            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }
}
