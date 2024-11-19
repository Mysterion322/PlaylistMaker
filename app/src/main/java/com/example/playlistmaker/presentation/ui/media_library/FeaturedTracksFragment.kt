package com.example.playlistmaker.presentation.ui.media_library

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentFeaturedTracksBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.debounce
import com.example.playlistmaker.presentation.ui.audio_player.AudioPlayerActivity
import com.example.playlistmaker.presentation.ui.search.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.presentation.ui.search.SearchFragment.Companion.INTENT_TRACK_KEY
import com.example.playlistmaker.presentation.ui.search.TrackAdapter
import com.example.playlistmaker.presentation.view_models.FeaturedTracksFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeaturedTracksFragment : Fragment() {

    private var _binding: FragmentFeaturedTracksBinding? = null
    private val binding get() = _binding!!
    private var debounceBoolean = true
    private lateinit var onTrackClickDebounce: (Unit) -> Unit
    private val trackAdapter: TrackAdapter by lazy {
        TrackAdapter(mutableListOf())
        { track ->
            if(debounceBoolean){
                debounceBoolean = false
                openPlayer(track)
                onTrackClickDebounce(Unit)
            } }
    }

    private val viewModel: FeaturedTracksFragmentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeaturedTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false
        ) { changeDebounceBoolean() }

        binding.recycleView.adapter = trackAdapter
        binding.recycleView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        viewModel.refreshFavoriteTracks()
        viewModel.getFavoriteTracks().observe(viewLifecycleOwner) { data ->
            if (data.isEmpty()) {
                binding.placeholderImage.isVisible = true
                binding.placeholderText.isVisible = true
                binding.recycleView.isVisible = false
            } else {
                updateFavoriteState(data)
                binding.placeholderImage.isVisible = false
                binding.placeholderText.isVisible = false
                binding.recycleView.isVisible = true
            }
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshFavoriteTracks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateFavoriteState(data: List<Track>) {
        trackAdapter.items = data.toMutableList()
        binding.recycleView.adapter = trackAdapter
    }

    private fun openPlayer(track: Track) {
        startActivity(AudioPlayerActivity.newInstance(requireContext(), track))
    }

    private fun changeDebounceBoolean(){
        debounceBoolean = true
    }

    companion object {
        fun newInstance() = FeaturedTracksFragment()
    }
}