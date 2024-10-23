package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(private val searchHistoryRepository: SearchHistoryRepository) :
    SearchHistoryInteractor {

    override fun getHistory(): MutableList<Track> {
        return searchHistoryRepository.getTracks()
    }

    override fun clearHistory() {
        searchHistoryRepository.clearHistory()
    }

    override fun addToHistory(track: Track) {
        searchHistoryRepository.addTrack(track)
    }

}