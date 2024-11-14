package com.example.playlistmaker.presentation.view_models

import android.app.Application
import android.provider.Settings.Global.getString
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.models.SearchResult
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.debounce
import com.example.playlistmaker.presentation.ui.search.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchViewModel(
    application: Application,
    private val trackInteractor: TrackInteractor,
    private val searchHistorySaver: SearchHistoryInteractor
) : AndroidViewModel(application) {

    private var latestSearchText: String = ""

    private var searchJob: Job? = null

    private val searchState = MutableLiveData<SearchState>()
    fun observeSearchState(): LiveData<SearchState> = searchState

    private val trackSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
            searchRequest(changedText)
        }

    fun addToHistory(track: Track) {
        searchHistorySaver.addToHistory(track)
        updateHistory()
    }

    fun clearHistory() {
        searchHistorySaver.clearHistory()
        renderState(SearchState.EmptyHistory)
    }

    fun updateHistory() {
        viewModelScope.launch {
            searchHistorySaver.getHistory().collect { tracks ->
                renderState(SearchState.ContentHistory(tracks))
            }
        }
    }

    private fun renderState(state: SearchState) {
        searchState.postValue(state)
    }

    init {
        updateHistory()
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isEmpty() || newSearchText.length < 2) {
            return
        }
        renderState(SearchState.Loading)
        searchJob = viewModelScope.launch {
            trackInteractor.search(newSearchText)
                .collect { result ->
                    processResult(
                        result
                    )
                }
        }
    }

    private fun processResult(searchResult: SearchResult) {
        when (searchResult) {
            is SearchResult.Success -> {
                val tracks = searchResult.tracks
                if (tracks.isNullOrEmpty()) {
                    renderState(SearchState.NothingFound)
                } else if (latestSearchText == searchResult.expression) {
                    renderState(SearchState.ContentSearch(tracks))
                }
            }

            is SearchResult.Error -> {
                val errorMessage = searchResult.message ?: getString(
                    getApplication(), R.string.search_internet_error.toString()
                )
                renderState(SearchState.Error(errorMessage))
            }

        }
    }

    fun searchDebounce(changedText: String) {
        stopSearch()
        if (latestSearchText == changedText || changedText.isEmpty() || changedText.length < 2) {
            return
        }
        latestSearchText = changedText
        trackSearchDebounce(changedText)
    }

    private fun stopSearch() {
        searchJob?.cancel()
    }


    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

}