package com.example.playlistmaker.presentation.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.AudioInteractor
import com.example.playlistmaker.domain.api.FavoritesInteractor
import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.models.AddToPlaylistState
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.audio_player.PlayingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val trackPlayerInteractor: AudioInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {

    private val playingState = MutableLiveData<PlayingState>(PlayingState.Default)
    private val positionState = MutableLiveData(0)
    fun observePlayingState(): LiveData<PlayingState> = playingState
    fun observePositionState(): LiveData<Int> = positionState

    private val favoriteState = MutableLiveData<Boolean>()
    fun observeFavoriteState(): LiveData<Boolean> = favoriteState

    private val addedToPlaylistState = MutableLiveData<AddToPlaylistState>()
    fun observeAddingToPlaylistState(): LiveData<AddToPlaylistState> = addedToPlaylistState

    private val playlists = MutableLiveData<List<Playlist>>()
    fun observePlaylists(): LiveData<List<Playlist>> = playlists

    private var updateJob: Job? = null

    fun onFavoriteClick(track: Track) {
        if (track.isFavorite) {
            viewModelScope.launch(Dispatchers.IO) {
                favoritesInteractor.removeFromFavorite(track)
            }
        }else{
                viewModelScope.launch(Dispatchers.IO) {
                    favoritesInteractor.addToFavorite(track)
                }
            }

        favoriteState.postValue(!track.isFavorite)
    }

    private fun startTimerCoroutine() {
        updateJob = viewModelScope.launch {
            while (true) {
                positionState.value = trackPlayerInteractor.getCurrentAudioPosition()
                delay(TIMER_UPDATE_DELAY)
            }
        }
    }

    private fun onPlay() {
        trackPlayerInteractor.startAudioPlayer()
        playingState.postValue(PlayingState.Playing)
        startTimerCoroutine()
    }

    fun onPause() {
        trackPlayerInteractor.pauseAudioPlayer()
        playingState.postValue(PlayingState.Paused)
        pauseTimer()
    }

    fun stateControl() {
        playingState.postValue(trackPlayerInteractor.getAudioState())
    }

    fun playingControl() {
        if (playingState.value == PlayingState.Playing) onPause()
        else onPlay()
    }

    private fun pauseTimer() {
        updateJob?.cancel()
    }

    fun onAddToPlaylistClick(trackId: String, playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            addedToPlaylistState.postValue(
                AddToPlaylistState(
                    playlistInteractor.addToPlaylist(
                        trackId,
                        playlist.id
                    ), playlist
                )
            )
        }
    }

    fun updatePlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.getPlaylists().collect {
                playlists.postValue(it)
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        pauseTimer()
        trackPlayerInteractor.releaseAudio()
    }

    companion object {
        private const val TIMER_UPDATE_DELAY = 300L
    }

}