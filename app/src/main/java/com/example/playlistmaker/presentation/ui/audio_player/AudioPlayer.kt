package com.example.playlistmaker.presentation.ui.audio_player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.models.PlayingState
import com.example.playlistmaker.presentation.ui.search.SearchActivity
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.view_models.AudioPlayerViewModel
import java.text.SimpleDateFormat
import java.util.Locale


class AudioPlayer : AppCompatActivity() {

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val binding by lazy { ActivityAudioPlayerBinding.inflate(layoutInflater) }
    private lateinit var viewModel: AudioPlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val track = intent.getParcelableExtra(SearchActivity.INTENT_TRACK_KEY) as? Track

        track?.let {
        Glide.with(this)
        .load(track.artworkUrl512)
        .placeholder(R.drawable.placeholder)
        .centerCrop()
        .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.api_image_radius)))
        .into(binding.ivAlbum)

    binding.tvMainAlbum.text = track.trackName
    binding.tvArtist.text = track.artistName
    binding.tvTrackTimeValue.text = track.trackTime
    binding.tvAlbumValue.text = track.trackName
    if (track.releaseDate != null) { binding.tvYearValue.text = track.releaseDate.substring(0, 4) }
    if (track.primaryGenreName != null) { binding.tvGenreValue.text = track.primaryGenreName }
    if (track.country != null) { binding.tvCountryValue.text = track.country }

            binding.ivPlayOrStop.isEnabled = true

            viewModel = ViewModelProvider(
                this,
                AudioPlayerViewModel.getViewModelFactory(track.previewUrl)
            )[AudioPlayerViewModel::class.java]
            viewModel.playingControl()
}
        viewModel.observePlayingState().observe(this) { state ->
            setPlayButtonImage(state)
            viewModel.stateControl()
        }

        viewModel.observePositionState().observe(this) {
            binding.tvTrackTimer.text = dateFormat.format(it)
        }

        binding.ivPlayOrStop.setOnClickListener {
            viewModel.playingControl()
        }


    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun setPlayButtonImage(state: PlayingState) {
        binding.ivPlayOrStop.setImageDrawable(
            AppCompatResources.getDrawable(
                this, when (state) {
                    PlayingState.Default,
                    PlayingState.Prepared,
                    PlayingState.Paused,
                    PlayingState.Complete,
                    -> R.drawable.play
                    PlayingState.Playing -> R.drawable.pause
                }
            )
        )
        if(state is PlayingState.Complete){
            binding.tvTrackTimer.text = "00:00"
        }
    }

}