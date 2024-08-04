package com.example.playlistmaker.presentation.ui.audio_player

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.search.SearchActivity
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Date


class AudioPlayer : AppCompatActivity() {

    private val STATE_DEFAULT = 0
    private val STATE_PREPARED = 1
    private val STATE_PLAYING = 2
    private val STATE_PAUSED = 3

    private var playerState = STATE_DEFAULT
    private lateinit var playIV: ImageView
    private lateinit var trackTimerTV: TextView
    private val mediaPlayer = MediaPlayer()
    private var url: String? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val buttonBack = findViewById<ImageView>(R.id.iv_back)
        buttonBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val track = intent.getParcelableExtra(SearchActivity.INTENT_TRACK_KEY) as? Track
        val albumImage = findViewById<ImageView>(R.id.iv_album)
        val albumMainText = findViewById<TextView>(R.id.tv_main_album)
        val artistText = findViewById<TextView>(R.id.tv_artist)
        val trackTimeText = findViewById<TextView>(R.id.tv_track_time_value)
        val albumSecondText = findViewById<TextView>(R.id.tv_album_value)
        val yearText = findViewById<TextView>(R.id.tv_year_value)
        val genreText = findViewById<TextView>(R.id.tv_genre_value)
        val countryText = findViewById<TextView>(R.id.tv_country_value)
        trackTimerTV = findViewById<TextView>(R.id.tv_track_timer)
        playIV = findViewById(R.id.iv_play_or_stop)
        url = track?.previewUrl
        if(url!=null){
            preparePlayer()
            playIV.setOnClickListener {
                playbackControl()
            }
        } else {
            playIV.setOnClickListener {
                Toast.makeText(applicationContext, "Track url is empty!", Toast.LENGTH_SHORT).show()
            }
        }

        Glide.with(this)
            .load(track?.artworkUrl512)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.api_image_radius)))
            .into(albumImage)

        albumMainText.text = track?.trackName
        artistText.text = track?.artistName
        trackTimeText.text = track?.trackTime
        albumSecondText.text = track?.trackName
        if(track?.releaseDate!=null){ yearText.text = track?.releaseDate.substring(0, 4) }
        if(track?.primaryGenreName!=null){ genreText.text = track?.primaryGenreName }
        if(track?.country!=null){ countryText.text = track?.country }


    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.release()
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

    private fun pausePlayer() {
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