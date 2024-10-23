package com.example.playlistmaker.presentation.ui.audio_player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.search.SearchFragment
import com.example.playlistmaker.presentation.view_models.AudioPlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale


class AudioPlayerActivity : AppCompatActivity() {

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val binding by lazy { ActivityAudioPlayerBinding.inflate(layoutInflater) }
    private val track by lazy { intent.getParcelableExtra(SearchFragment.INTENT_TRACK_KEY) as? Track }
    private val viewModel: AudioPlayerViewModel by viewModel {
        parametersOf(track?.previewUrl)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val track = intent.getParcelableExtra(SearchFragment.INTENT_TRACK_KEY) as? Track

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
            if (track.releaseDate != null) {
                binding.tvYearValue.text = track.releaseDate.substring(0, 4)
            }
            if (track.primaryGenreName != null) {
                binding.tvGenreValue.text = track.primaryGenreName
            }
            if (track.country != null) {
                binding.tvCountryValue.text = track.country
            }

            binding.ivPlayOrStop.isEnabled = true

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
        when (state) {
            PlayingState.Default,
            PlayingState.Paused,
            -> binding.ivPlayOrStop.setImageDrawable(
                AppCompatResources.getDrawable(
                    this, R.drawable.play
                )
            )

            PlayingState.Playing -> binding.ivPlayOrStop.setImageDrawable(
                AppCompatResources.getDrawable(
                    this, R.drawable.pause
                )
            )

            PlayingState.Complete -> {
                binding.ivPlayOrStop.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this, R.drawable.play
                    )
                )
                binding.tvTrackTimer.text = "00:00"
            }
        }
    }


}