package com.example.playlistmaker.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.AudioRepository
import com.example.playlistmaker.presentation.ui.audio_player.PlayingState

class AudioRepositoryImpl(private val url: String?) : AudioRepository {

    private val mediaPlayer = MediaPlayer()
    private var state: PlayingState = PlayingState.Default

    init {
        preparePlayer()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnCompletionListener {
            state = PlayingState.Complete
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        state = PlayingState.Playing
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        state = PlayingState.Paused
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun getState(): PlayingState {
        return state
    }

}