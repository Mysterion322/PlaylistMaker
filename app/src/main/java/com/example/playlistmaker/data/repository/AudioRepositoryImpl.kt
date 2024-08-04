package com.example.playlistmaker.data.repository

import android.media.MediaPlayer
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.AudioInteractor
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Date

class AudioRepositoryImpl (private val mediaPlayer: MediaPlayer,
                           private val playIV: ImageView,
                           private val handler: Handler,
                           private val trackTimerTV: TextView,
                           val track: Track?): AudioInteractor {

    private val STATE_DEFAULT = 0
    private val STATE_PREPARED = 1
    private val STATE_PLAYING = 2
    private val STATE_PAUSED = 3

    private var playerState = STATE_DEFAULT
    private var url: String? = null

    init {
        url = track?.previewUrl
        if(url!=null){
            preparePlayer()
            playIV.setOnClickListener {
                playbackControl()
            }
        }
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playIV.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playIV.setImageResource(R.drawable.play)
            trackTimerTV.text = "00:00"
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playIV.setImageResource(R.drawable.pause)
        playerState = STATE_PLAYING
        handler.post(updateTimer())
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playIV.setImageResource(R.drawable.play)
        playerState = STATE_PAUSED
    }

    private fun updateTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    val elapsedTime = mediaPlayer.getCurrentPosition()
                    trackTimerTV.text = formatMilliseconds(elapsedTime.toLong())
                    handler.postDelayed(this, 1000L)
                }
            }
        }
    }

    private fun formatMilliseconds(milliseconds: Long): String {
        val format = SimpleDateFormat("mm:ss")
        return format.format(Date(milliseconds))
    }

}