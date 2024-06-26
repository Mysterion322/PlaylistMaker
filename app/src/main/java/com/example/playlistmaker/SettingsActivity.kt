package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

const val THEME_KEY = "key_for_theme"
const val THEME = "day_night_theme"
class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val context = applicationContext

        val buttonSettings = findViewById<ImageView>(R.id.back_settings_image)
        buttonSettings.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val stringValueShare = context.getString(R.string.uri_share)

        val buttonShare = findViewById<ImageView>(R.id.imageShare)
        buttonShare.setOnClickListener {
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

        val buttonSupport = findViewById<ImageView>(R.id.image_support)
        buttonSupport.setOnClickListener {
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:" + stringValueMail)
                putExtra(Intent.EXTRA_SUBJECT, stringValueSubject)
                putExtra(Intent.EXTRA_TEXT, stringValueText)
                startActivity(this)
            }
        }

        val stringValueTermsOfUse = context.getString(R.string.uri_terms_of_use)

        val buttonTermsOfUse = findViewById<ImageView>(R.id.image_terms_of_use)
        buttonTermsOfUse.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(stringValueTermsOfUse))
            startActivity(browserIntent)
        }

        val switchDarkTheme = findViewById<Switch>(R.id.switchDarkTheme)
        val sharedPrefs = getSharedPreferences(THEME, MODE_PRIVATE)
        val checked = sharedPrefs.getBoolean(THEME_KEY, false)
        switchDarkTheme.isChecked = checked
        switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
            sharedPrefs.edit().putBoolean(THEME_KEY, isChecked).apply()
        }



    }


}