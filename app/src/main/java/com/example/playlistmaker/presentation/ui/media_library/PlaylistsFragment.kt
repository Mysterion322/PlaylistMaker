package com.example.playlistmaker.presentation.ui.media_library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.presentation.ui.new_playlist_fragment.NewPlaylistFragment
import com.example.playlistmaker.presentation.view_models.PlaylistsFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistsFragmentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = GridLayoutManager(
            context, 2
        )

        viewModel.observePlaylists().observe(viewLifecycleOwner) { state ->
            binding.recyclerView.adapter = PlaylistAdapter(state)
            render(state.size)
        }

        binding.createPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_newPlaylistFragment)
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Any>(
            NewPlaylistFragment.RESULT
        )?.observe(viewLifecycleOwner) { result ->
            viewModel.updatePlaylists()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.updatePlaylists()
    }

    private fun render(size: Int) {
        if (size > 0) {
            binding.mediaPlaceholderIv.visibility = View.GONE
            binding.mediaPlaceholderTv.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        } else {
            binding.mediaPlaceholderIv.visibility = View.VISIBLE
            binding.mediaPlaceholderTv.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}