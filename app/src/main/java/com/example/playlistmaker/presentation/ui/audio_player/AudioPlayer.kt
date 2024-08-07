package com.example.playlistmaker.presentation.ui.audio_player

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.AudioInteractor
import com.example.playlistmaker.presentation.ui.search.SearchActivity
import com.example.playlistmaker.domain.models.Track


class AudioPlayer : AppCompatActivity() {

    private lateinit var playIV: ImageView
    private lateinit var trackTimerTV: TextView
    private val mediaPlayer = MediaPlayer()
    private lateinit var audioInteractor: AudioInteractor
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
        audioInteractor = Creator.provideAudioInteractor(mediaPlayer, playIV, handler, trackTimerTV, track)

        track?.let {
        Glide.with(this)
        .load(track.artworkUrl512)
        .placeholder(R.drawable.placeholder)
        .centerCrop()
        .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.api_image_radius)))
        .into(albumImage)

    albumMainText.text = track.trackName
    artistText.text = track.artistName
    trackTimeText.text = track.trackTime
    albumSecondText.text = track.trackName
    if (track.releaseDate != null) { yearText.text = track.releaseDate.substring(0, 4) }
    if (track.primaryGenreName != null) { genreText.text = track.primaryGenreName }
    if (track.country != null) { countryText.text = track.country }
}

    }

    override fun onPause() {
        super.onPause()
        audioInteractor.pauseAudio()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.release()
    }


}