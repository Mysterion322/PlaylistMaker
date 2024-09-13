package com.example.playlistmaker.presentation.ui.media_library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFeaturedTracksBinding
import com.example.playlistmaker.presentation.view_models.FeaturedTracksFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeaturedTracksFragment : Fragment() {

    private var _binding: FragmentFeaturedTracksBinding? = null
    private val binding get() = _binding!!

    private val featuredTracksViewModel: FeaturedTracksFragmentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeaturedTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FeaturedTracksFragment()
    }
}