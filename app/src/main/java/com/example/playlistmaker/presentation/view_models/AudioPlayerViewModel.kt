package com.example.playlistmaker.presentation.view_models

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.api.AudioInteractor
import com.example.playlistmaker.domain.api.AudioRepository
import com.example.playlistmaker.presentation.ui.audio_player.PlayingState

class AudioPlayerViewModel(
    private val trackPlayerInteractor: AudioInteractor,
) : ViewModel() {

    private val playingState = MutableLiveData<PlayingState>(PlayingState.Default)
    private val positionState = MutableLiveData(0)
    fun observePlayingState(): LiveData<PlayingState> = playingState
    fun observePositionState(): LiveData<Int> = positionState

    companion object {
        private const val TIMER_UPDATE_DELAY = 250L
        fun getViewModelFactory(trackUrl: String): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    AudioPlayerViewModel(
                        trackPlayerInteractor = Creator.provideAudioInteractor(trackUrl),
                    )
                }
            }
    }

    private fun onPlay() {
        trackPlayerInteractor.startAudioPlayer()
        playingState.postValue(PlayingState.Playing)
        startTimer()
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

    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable by lazy {
        object : Runnable {
            override fun run() {
                if (playingState.value == PlayingState.Playing) {
                    positionState.postValue(trackPlayerInteractor.getCurrentAudioPosition())
                    handler.postDelayed(this, TIMER_UPDATE_DELAY)
                }
            }
        }
    }

    private fun startTimer() {
        handler.post(timerRunnable)
    }

    private fun pauseTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    override fun onCleared() {
        super.onCleared()
        pauseTimer()
        trackPlayerInteractor.releaseAudio()
    }

}