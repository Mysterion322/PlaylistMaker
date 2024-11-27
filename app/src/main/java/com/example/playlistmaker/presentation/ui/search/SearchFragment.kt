package com.example.playlistmaker.presentation.ui.search

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.debounce
import com.example.playlistmaker.presentation.ui.audio_player.AudioPlayerActivity
import com.example.playlistmaker.presentation.view_models.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var searchValue = TEXT_DEF
    private var debounceBoolean = true

    private val viewModel by viewModel<SearchViewModel>()

    private var searchAdapter: TrackAdapter? = null
    private lateinit var historyAdapter: TrackAdapter

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var onTrackClickDebounce: (Unit) -> Unit

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val TEXT_DEF = ""
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivClearEditText.setOnClickListener {
            binding.searchEditText.text.clear()
            clearSearchAdapter()
        }

        binding.searchEditText.requestFocus()
        binding.searchEditText.setText(searchValue)
        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isEmpty()) {
                viewModel.updateHistory()
            } else {
                showContentSearch()
            }
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            binding.llHistorySearch.isVisible = false
        }

        binding.searchEditText.doOnTextChanged { s, _, _, _ ->
            clearButtonVisibility(s, binding.ivClearEditText)
            searchValue = s.toString()
            if (binding.searchEditText.hasFocus() && s?.isEmpty() == true) {
                clearSearchAdapter()
                viewModel.updateHistory()
            } else viewModel.searchDebounce(searchValue)
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchDebounce(searchValue)
            }
            false
        }

        onTrackClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false
        ) { debounceBoolean = true }

        val onItemClickListener = OnItemClickListener { item ->
            if (debounceBoolean) {
                debounceBoolean = false
                openPlayer(item)
                onTrackClickDebounce(Unit)
            }
        }
        historyAdapter = TrackAdapter(onItemClickListener)
        binding.rvHistoryTrackList.layoutManager = LinearLayoutManager(context)
        binding.rvHistoryTrackList.adapter = historyAdapter
        binding.rvTrackList.layoutManager = LinearLayoutManager(context)
        searchAdapter = TrackAdapter(onItemClickListener)
        binding.rvTrackList.adapter = searchAdapter

        binding.updateButton.setOnClickListener {
            viewModel.updateSearch()
        }
        viewModel.observeSearchState().observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun clearSearchAdapter() {
        viewModel.stopSearch()
        searchAdapter?.clearItems()
    }

    private fun renderState(state: SearchState) {
        when (state) {
            is SearchState.ContentHistory -> updateSearchHistoryAdapter(state)
            is SearchState.ContentSearch -> updateContentSearch(state.tracks)
            is SearchState.Error -> showErrorMessage(state.errorMessage)
            is SearchState.Loading -> showLoading(true)
            is SearchState.NothingFound -> showEmptyView()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
    }

    private fun showContentSearch() {
        binding.rvTrackList.isVisible = true
        binding.progressBar.isVisible = false
        binding.llHistorySearch.isVisible = false
    }

    private fun updateContentSearch(tracks: List<Track>) {
        showLoading(false)
        searchAdapter?.items?.clear()
        searchAdapter?.items?.addAll(tracks)
        searchAdapter?.itemCount?.let { searchAdapter?.notifyItemRangeChanged(0, it) }
        showContentSearch()
    }

    private fun showHistory() {
        binding.rvTrackList.isVisible = false
        binding.progressBar.isVisible = false
        binding.llHistorySearch.isVisible = (historyAdapter.itemCount != 0)
    }

    private fun updateSearchHistoryAdapter(sdata: SearchState) {
        historyAdapter.clearItems()
        if (sdata is SearchState.ContentHistory && sdata.data.isNotEmpty()) {
            historyAdapter.items.addAll(sdata.data)
            historyAdapter.notifyItemRangeChanged(0, historyAdapter.itemCount)
        }
        showHistory()
    }

    private fun openPlayer(track: Track) {

        viewModel.addToHistory(track)
        startActivity(AudioPlayerActivity.newInstance(requireContext(), track))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchValue)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            searchValue = savedInstanceState.getString(SEARCH_TEXT, TEXT_DEF)
        }
    }

    private fun clearButtonVisibility(s: CharSequence?, v: ImageView) {
        if (s.isNullOrEmpty()) {
            v.isVisible = false
            view?.let { activity?.hideKeyboard() }
        } else {
            v.isVisible = true
        }
    }

    private fun Activity.hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus
        if (view != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showEmptyView() {
        showLoading(false)
        binding.rvTrackList.isVisible = false
        binding.llHistorySearch.isVisible = false
        binding.llNoInternet.isVisible = false
        binding.llNotFound.isVisible = true
        searchAdapter?.let { searchAdapter?.notifyItemRangeChanged(0, it.itemCount) }

    }

    private fun showErrorMessage(additionalMessage: String) {
        showLoading(false)
        binding.rvTrackList.isVisible = false
        binding.llHistorySearch.isVisible = false
        binding.llNoInternet.isVisible = true
        binding.llNotFound.isVisible = false
        searchAdapter?.let { searchAdapter?.notifyItemRangeChanged(0, it.itemCount) }
        if (additionalMessage.isNotEmpty()) {
            Toast.makeText(context, additionalMessage, Toast.LENGTH_LONG).show()
        }
    }
}

