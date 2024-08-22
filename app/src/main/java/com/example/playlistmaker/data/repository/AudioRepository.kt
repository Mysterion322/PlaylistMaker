package com.example.playlistmaker.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.AudioInteractor
import com.example.playlistmaker.domain.models.PlayingState

class AudioRepository (val url: String?): AudioInteractor {

    private val mediaPlayer = MediaPlayer()
    override var state: PlayingState = PlayingState.Default

    override fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            state = PlayingState.Prepared
        }
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



}