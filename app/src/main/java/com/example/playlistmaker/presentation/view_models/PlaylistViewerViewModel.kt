package com.example.playlistmaker.presentation.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistViewerViewModel(private val interactor: PlaylistInteractor) : ViewModel() {

    private val playlist = MutableLiveData<Playlist?>()
    fun observePlaylist(): LiveData<Playlist?> = playlist

    private val allTracks = MutableLiveData<List<Track>?>()
    fun observeAllTracks(): LiveData<List<Track>?> = allTracks


    fun loadPlaylist(playlistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.getPlaylistById(playlistId).collect {
                if (it != null) {
                    interactor.getAllTracks(it.id).collect { tracks ->
                        allTracks.postValue(tracks?.reversed())
                    }
                    playlist.postValue(it)
                }

            }
        }
    }

    fun removeTrackFromPlaylist(trackId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.removeFromPlaylist(trackId, playlist.value!!.id)
            interactor.getAllTracks(playlist.value!!.id).collect { tracks ->
                allTracks.postValue(tracks)
            }
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.deletePlaylist(playlist.value!!.id)
        }
    }
}