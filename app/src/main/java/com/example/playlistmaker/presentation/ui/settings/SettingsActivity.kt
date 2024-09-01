package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.presentation.view_models.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val context = applicationContext

        binding.backSettingsImage.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

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
            (applicationContext as App).switchTheme(isChecked)
            viewModel.updateThemeState(isChecked)
        }


    }


}