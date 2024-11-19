package com.example.playlistmaker.presentation.ui.audio_player

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.IntentCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.debounce
import com.example.playlistmaker.presentation.ui.media_library.OnItemClickListener
import com.example.playlistmaker.presentation.ui.media_library.PlaylistHorizontalAdapter
import com.example.playlistmaker.presentation.ui.new_playlist_fragment.NewPlaylistFragment
import com.example.playlistmaker.presentation.view_models.AudioPlayerViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale


class AudioPlayerActivity : AppCompatActivity() {

    private val binding: ActivityAudioPlayerBinding by lazy {
        ActivityAudioPlayerBinding.inflate(layoutInflater)
    }
    private val dateFormat by lazy { SimpleDateFormat(TIME_PATTERN, Locale.getDefault()) }
    private var bottomSheetState = BottomSheetBehavior.STATE_HIDDEN
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit
    private val track by lazy { IntentCompat.getParcelableExtra(intent, TRACK_KEY, Track::class.java) }
    private val viewModel: AudioPlayerViewModel by viewModel {
        parametersOf(track?.previewUrl)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        updateFavoriteState(track?.isFavorite ?: false)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ivLike.setOnClickListener {
            viewModel.onFavoriteClick(track!!)
        }

        viewModel.observeFavoriteState().observe(this) { state ->
            updateFavoriteState(state)
        }

            Glide.with(this)
                .load(track?.artworkUrl512)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.api_image_radius)))
                .into(binding.ivAlbum)

            binding.tvMainAlbum.text = track?.trackName
            binding.tvArtist.text = track?.artistName
            binding.tvTrackTimeValue.text = track?.trackTime
            binding.tvAlbumValue.text = track?.trackName
            if (track?.releaseDate != null) {
                binding.tvYearValue.text = track!!.releaseDate.substring(0, 4)
            }
            if (track?.primaryGenreName != null) {
                binding.tvGenreValue.text = track!!.primaryGenreName
            }
            if (track?.country != null) {
                binding.tvCountryValue.text = track!!.country
            }

            binding.ivPlayOrStop.isEnabled = true

            viewModel.playingControl()

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





            val bottomSheetContainer = binding.playlistsBottomSheet
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            bottomSheetBehavior.peekHeight = binding.root.height / 3 * 2
                        }

                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.overlay.visibility = View.GONE
                        }

                        else -> {
                            binding.overlay.visibility = View.VISIBLE
                            viewModel.updatePlaylists()
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.overlay.alpha = (slideOffset + 1f) / 2f
                }
            })

            binding.ivAdd.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            binding.createPlaylistButton.setOnClickListener {
                bottomSheetState = bottomSheetBehavior.state
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

                val fragment = NewPlaylistFragment()
                supportFragmentManager.beginTransaction().replace(R.id.fragment_view, fragment)
                    .addToBackStack(null).commit()
                binding.playerContent.visibility = View.GONE
                binding.fragmentView.visibility = View.VISIBLE
            }

            supportFragmentManager.setFragmentResultListener(
                NewPlaylistFragment.RESULT, this
            ) { _, bundle ->
                bottomSheetBehavior.state = bottomSheetState
                binding.playerContent.visibility = View.VISIBLE
                binding.fragmentView.visibility = View.GONE
            }




            val onItemClickListener = OnItemClickListener { item ->
                onPlaylistClickDebounce(item)
            }
            onPlaylistClickDebounce = debounce(
                CLICK_DEBOUNCE_DELAY, lifecycleScope, false
            ) { item ->
                viewModel.onAddToPlaylistClick(track!!.trackId, item)
            }

            viewModel.observeAddingToPlaylistState().observe(this) { state ->
                when (state.isAdded) {
                    true -> {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        Toast.makeText(
                            this,
                            getString(R.string.playlist_added).format(state.playlist.name),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    false -> {
                        Toast.makeText(
                            this,
                            getString(R.string.playlist_not_added).format(state.playlist.name),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            viewModel.observePlaylists().observe(this) { state ->
                binding.recyclePlaylistsView.adapter =
                    PlaylistHorizontalAdapter(state, onItemClickListener)
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

    private fun updateFavoriteState(isFavorite: Boolean) {
        if (isFavorite) {
            binding.ivLike.setImageResource(R.drawable.added_like)
        } else {
            binding.ivLike.setImageResource(R.drawable.like)
        }
    }


    companion object {
        private const val TIME_PATTERN = "mm:ss"
        const val TRACK_KEY = "TRACK_KEY"
        private const val CLICK_DEBOUNCE_DELAY = 300L

        fun newInstance(context: Context, track: Track): Intent {
            return Intent(context, AudioPlayerActivity::class.java).apply {
                putExtra(TRACK_KEY, track)
            }
        }
    }
}