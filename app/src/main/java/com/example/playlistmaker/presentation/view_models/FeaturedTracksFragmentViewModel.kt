package com.example.playlistmaker.presentation.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.FavoritesInteractor
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.launch

class FeaturedTracksFragmentViewModel(private val favoritesInteractor: FavoritesInteractor) :
    ViewModel() {

    private val favoriteTracks = MutableLiveData<List<Track>>()
    fun getFavoriteTracks(): LiveData<List<Track>> = favoriteTracks

    fun refreshFavoriteTracks() {
        viewModelScope.launch {
            favoritesInteractor.getFavoriteTracks().collect { trackList ->
                favoriteTracks.postValue(trackList)
            }
        }
    }

}