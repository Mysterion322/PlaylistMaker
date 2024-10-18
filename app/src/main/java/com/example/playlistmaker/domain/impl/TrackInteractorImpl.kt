package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Resource
import com.example.playlistmaker.domain.models.SearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {
    override fun search(expression: String): Flow<SearchResult> {
        return repository.searchTracks(expression).map { result ->
            when (result) {
                is Resource.Success -> SearchResult.Success(result.data, expression)
                is Resource.Error -> SearchResult.Error(result.message)

            }
        }
    }
}