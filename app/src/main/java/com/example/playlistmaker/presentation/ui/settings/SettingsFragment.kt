package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.presentation.view_models.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()

        val stringValueShare = context.getString(R.string.uri_share)

        binding.imageShare.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, stringValueShare)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        val stringValueMail = context.getString(R.string.my_mail)
        val stringValueSubject = context.getString(R.string.support_subject_message)
        val stringValueText = context.getString(R.string.support_text_message)

        binding.imageSupport.setOnClickListener {
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:" + stringValueMail)
                putExtra(Intent.EXTRA_SUBJECT, stringValueSubject)
                putExtra(Intent.EXTRA_TEXT, stringValueText)
                startActivity(this)
            }
        }

        val stringValueTermsOfUse = context.getString(R.string.uri_terms_of_use)

        binding.imageTermsOfUse.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(stringValueTermsOfUse))
            startActivity(browserIntent)
        }

        binding.switchDarkTheme.isChecked = viewModel.observeThemeState().value ?: false
        binding.switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            (requireActivity().application as App).switchTheme(isChecked)
            viewModel.updateThemeState(isChecked)
        }

    }


}