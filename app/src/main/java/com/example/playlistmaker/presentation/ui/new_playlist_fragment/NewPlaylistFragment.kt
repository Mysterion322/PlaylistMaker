package com.example.playlistmaker.presentation.ui.new_playlist_fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewplaylistBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.convertDpToPx
import com.example.playlistmaker.presentation.getDefaultCacheImagePath
import com.example.playlistmaker.presentation.view_models.NewPlaylistViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class NewPlaylistFragment() : Fragment() {

    private var _binding: FragmentNewplaylistBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri = Uri.EMPTY
    private val viewModel by viewModel<NewPlaylistViewModel>()
    private var fromNavController = true
    private var playlistId: Int = -1
    private var _playlist: Playlist? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentNewplaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
        fromNavController = requireArguments().getBoolean(FROM_NAVCONTROLLER_KEY, true)
        playlistId = requireArguments().getInt(PLAYLIST_ID_KEY)

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        val divider = requireActivity().findViewById<View>(R.id.divider)
        if (bottomNavigationView != null && divider != null) {
            bottomNavigationView.visibility = GONE
            divider.visibility = GONE
        }
        binding.btnCreate.isEnabled = false
        binding.ivBack.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
        binding.playlistName.doOnTextChanged { s, _, _, _ ->
            binding.btnCreate.isEnabled = s?.isNotEmpty() == true

        }

        if (playlistId > 0) {
            viewModel.getPlaylist(playlistId)
        }

        viewModel.observePlaylist().observe(viewLifecycleOwner) { playlist ->
            binding.tvTitle.text = "Редактировать"
            binding.btnCreate.text = "Сохранить"
            if (playlist != null) {
                _playlist = playlist
                if (playlist.imagePath.isNullOrEmpty()) {
                    binding.image.setImageDrawable(
                        getDrawable(requireContext(), R.drawable.placeholder)
                    )
                    binding.image.scaleType = ImageView.ScaleType.CENTER_CROP
                } else {
                    binding.image.setImageURI(
                        File(
                            getDefaultCacheImagePath(requireContext()), playlist.imagePath
                        ).toUri()
                    )
                }
                binding.playlistName.setText(playlist.name)
                binding.playlistDescription.setText((playlist.description))
            }
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.image.scaleType = ImageView.ScaleType.CENTER_CROP
                    Glide.with(this)
                        .load(uri)
                        .apply(
                            RequestOptions().transform(
                                MultiTransformation(
                                    CenterCrop(),
                                    RoundedCorners(
                                        convertDpToPx(
                                            8f,
                                            requireContext()
                                        )
                                    )
                                )
                            )
                        )
                        .into(binding.image)
                    imageUri = uri
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.image.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnCreate.setOnClickListener {
            val playlistName = binding.playlistName.text.toString()
            if (playlistId < 1) {
                viewModel.createPlaylist(
                    playlistName,
                    binding.playlistDescription.text.toString(),
                    imageUri,
                    binding.image.drawable.toBitmap()
                )
                Toast.makeText(
                    context,
                    context?.getString(R.string.playlist_created)?.format(playlistName),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.updatePlaylist(
                    playlistName,
                    binding.playlistDescription.text.toString(),
                    binding.image.drawable.toBitmap()
                )
            }

            closeFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.playlistName.text.toString().isNotEmpty() && playlistId < 1) {
                MaterialAlertDialogBuilder(context!!).setTitle(R.string.exit_title)
                    .setMessage(R.string.exit_message)
                    .setNeutralButton(android.R.string.cancel) { dialog, which ->

                    }.setPositiveButton(R.string.finish) { dialog, which ->
                        closeFragment()
                    }.show()
            } else closeFragment()
        }
    }

    private fun closeFragment() {
        val result = Bundle()
        if (fromNavController) {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(RESULT, result)
        } else {
            parentFragmentManager.setFragmentResult(RESULT, result)
        }

        parentFragmentManager.popBackStack()
    }

    companion object {
        const val RESULT = "RESULT_KEY"
        const val FROM_NAVCONTROLLER_KEY = "FROM_NAVCONTROLLER_KEY"
        const val PLAYLIST_ID_KEY = "PLAYLIST_ID_KEY"
        fun newInstance(fromNavController: Boolean, playlistId: Int = -1) =
            NewPlaylistFragment().apply {
                arguments = bundleOf(
                    FROM_NAVCONTROLLER_KEY to fromNavController, PLAYLIST_ID_KEY to playlistId
                )
            }
    }
}
