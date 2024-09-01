package com.example.playlistmaker.presentation.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.audio_player.AudioPlayerActivity
import com.example.playlistmaker.presentation.view_models.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySearchBinding.inflate(layoutInflater) }
    private val viewModel by viewModel<SearchViewModel>()
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private var text: String = EMPTY
    private val trackList = mutableListOf<Track>()
    private val trackAdapter: TrackAdapter by lazy {
        TrackAdapter(trackList) { track ->
            if (clickDebounce()) {
                viewModel.addToHistory(track)
                val audioPlayerIntent = Intent(this, AudioPlayerActivity::class.java)
                startActivity(audioPlayerIntent.putExtra(INTENT_TRACK_KEY, track))
            } } }
    private val trackAdapterHistory: TrackAdapter by lazy { TrackAdapter(mutableListOf())
    { track ->
        if (clickDebounce()) {
            viewModel.addToHistory(track)
            val audioPlayerIntent = Intent(this, AudioPlayerActivity::class.java)
            startActivity(audioPlayerIntent.putExtra(INTENT_TRACK_KEY, track))
        }}  }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.observeSearchState().observe(this) { state ->
            renderState(state)
        }
        binding.rvTrackList.adapter = trackAdapter
        binding.rvTrackList.layoutManager = LinearLayoutManager(
            this@SearchActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvHistoryTrackList.adapter = trackAdapterHistory
        binding.rvHistoryTrackList.layoutManager = LinearLayoutManager(
            this@SearchActivity,
            LinearLayoutManager.VERTICAL,
            false
        )

        if(text.isEmpty()&&trackAdapterHistory.itemCount!=0){
            binding.llHistorySearch.isVisible = false
        }

        binding.ivClearEditText.setOnClickListener {
            binding.searchEditText.setText(EMPTY)
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            trackAdapterHistory.notifyDataSetChanged()
            binding.llNotFound.isVisible = false
            binding.llNoInternet.isVisible = false
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Здесь можно добавить любой нужный код перед изменением текста
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                text = s.toString()
                if (binding.searchEditText.text.length == 0) {
                    binding.ivClearEditText.isVisible = false
                } else {
                    binding.ivClearEditText.isVisible = true
                    viewModel.searchDebounce(text)
                }
                // Здесь можно добавить любой нужный код при изменении текста
            }

            override fun afterTextChanged(s: Editable?) {

                binding.llHistorySearch.isVisible = text.isEmpty()&&trackAdapterHistory.itemCount!=0

                // Здесь можно добавить любой нужный код после изменения текста
            }
        })

        binding.backSearchImage.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.updateButton.setOnClickListener {
            if(clickDebounce()) {
                viewModel.searchRequest(text)
            }
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            binding.llHistorySearch.isVisible = false
            trackAdapterHistory.notifyDataSetChanged()
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE&&clickDebounce()) {
                viewModel.searchRequest(text)
            }
            false
        }

    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(KEY, binding.searchEditText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        binding.searchEditText.setText(savedInstanceState.getString(KEY, EMPTY))
    }

    private fun renderState(state: SearchState) {
        when (state) {
            is SearchState.ContentHistory -> updateSearchHistoryAdapter(state)
            is SearchState.ContentSearch -> updateContentSearch(state.tracks)
            is SearchState.EmptyHistory -> updateSearchHistoryAdapter(state)
            is SearchState.Error -> showErrorMessage(state.errorMessage)
            is SearchState.Loading -> showLoading(true)
            is SearchState.NothingFound -> showEmptyView()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if(text.isEmpty()){return}
        binding.progressBar.isVisible = isLoading
    }

    private fun showContentSearch() {
        binding.llNotFound.isVisible = false
        binding.llNoInternet.isVisible = false
        binding.progressBar.isVisible = false
        binding.rvTrackList.isVisible = true
    }

    private fun updateContentSearch(tracks: List<Track>) {
        showLoading(false)
        trackList.clear()
        trackList.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
        showContentSearch()
    }

    private fun showHistory() {
        binding.rvTrackList.isVisible = false
        binding.llHistorySearch.isVisible = (trackAdapterHistory.itemCount != 0)
        binding.llNotFound.isVisible = false
        binding.llNoInternet.isVisible = false
        binding.progressBar.isVisible = false

    }

    private fun updateSearchHistoryAdapter(sdata: SearchState) {
        trackAdapterHistory.items.clear()
        if (sdata is SearchState.ContentHistory) {
            trackAdapterHistory.items.addAll(sdata.data)
        }
        trackAdapterHistory.notifyDataSetChanged()
        showHistory()
    }

    private fun showEmptyView() {
        showLoading(false)
        trackList.clear()
        binding.llNoInternet.isVisible = false
        binding.progressBar.isVisible = false
        binding.llNotFound.isVisible = true
    }

    private fun showErrorMessage(additionalMessage: String) {
        showLoading(false)
        trackList.clear()
        binding.llNotFound.isVisible = false
        binding.progressBar.isVisible = false
        binding.llNoInternet.isVisible = true
        if (additionalMessage.isNotEmpty()) {
            Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                .show()
        }
    }


    companion object {
        private const val KEY = "SEARCH_TEXT"
        private const val CLICK_DEBOUNCE_DELAY: Long = 1000
        const val INTENT_TRACK_KEY = "intent_track"
        private const val EMPTY = ""
    }

}