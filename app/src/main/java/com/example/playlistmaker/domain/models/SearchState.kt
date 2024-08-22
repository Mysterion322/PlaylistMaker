package com.example.playlistmaker.domain.models

sealed interface SearchState {
    data class ContentHistory(val data: List<Track>) : SearchState
    data object EmptyHistory : SearchState
    data object Loading : SearchState
    data class ContentSearch(val tracks: List<Track>) : SearchState
    data class Error(val errorMessage: String) : SearchState
    data object NothingFound : SearchState
}