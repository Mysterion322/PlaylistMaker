package com.example.playlistmaker.domain.api


import com.example.playlistmaker.domain.models.SearchResult
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {
    fun search(expression: String): Flow<SearchResult>
}