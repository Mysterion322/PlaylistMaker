package com.example.playlistmaker.presentation.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.AudioInteractor
import com.example.playlistmaker.domain.api.FavoritesInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.audio_player.PlayingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val trackPlayerInteractor: AudioInteractor,
    private val favoritesInteractor: FavoritesInteractor,
) : ViewModel() {

    private val playingState = MutableLiveData<PlayingState>(PlayingState.Default)
    private val positionState = MutableLiveData(0)
    fun observePlayingState(): LiveData<PlayingState> = playingState
    fun observePositionState(): LiveData<Int> = positionState

    private val favoriteState = MutableLiveData<Boolean>()
    fun observeFavoriteState(): LiveData<Boolean> = favoriteState

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

    override fun onCleared() {
        super.onCleared()
        pauseTimer()
        trackPlayerInteractor.releaseAudio()
    }

    companion object {
        private const val TIMER_UPDATE_DELAY = 300L
    }

}