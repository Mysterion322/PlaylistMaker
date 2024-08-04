package com.example.playlistmaker.presentation.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.audio_player.AudioPlayer
import com.google.android.material.progressindicator.CircularProgressIndicator

class SearchActivity : AppCompatActivity() {

    private var isClickAllowed = true
    private val searchRunnable = Runnable { apiRequest(text) }
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var editTextSearch: EditText
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var notFound: LinearLayout
    private lateinit var noInternet: LinearLayout
    private lateinit var historyLL: LinearLayout
    private lateinit var searchHistory: SearchHistoryInteractor
    private var text: String = EMPTY
    private val trackList = mutableListOf<Track>()
    private val trackAdapter: TrackAdapter by lazy {
        TrackAdapter(trackList) { track ->
            if (clickDebounce()) {
                searchHistory.addTrack(track)
                val audioPlayerIntent = Intent(this, AudioPlayer::class.java)
                startActivity(audioPlayerIntent.putExtra(INTENT_TRACK_KEY, track))
            }
        }
    }
    private lateinit var trackAdapterHistory: TrackAdapter
    private lateinit var rvTrackList: RecyclerView
    private lateinit var rvTrackListHistory: RecyclerView


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        rvTrackList = findViewById<RecyclerView>(R.id.rv_track_list)
        rvTrackListHistory = findViewById<RecyclerView>(R.id.rv_history_track_list)
        editTextSearch = findViewById<EditText>(R.id.search_edit_text)
        notFound = findViewById<LinearLayout>(R.id.ll_not_found)
        noInternet = findViewById<LinearLayout>(R.id.ll_no_internet)
        historyLL = findViewById<LinearLayout>(R.id.ll_history_search)
        progressBar = findViewById(R.id.progressBar)
        val updateButton = findViewById<Button>(R.id.update_button)
        val clearHistoryButton = findViewById<Button>(R.id.clear_history_button)
        val clearEditText = findViewById<ImageView>(R.id.iv_clear_edit_text)
        val audioPlayerIntent = Intent(this, AudioPlayer::class.java)
        searchHistory = Creator.provideSearchHistoryRepository()
        trackAdapterHistory = TrackAdapter(searchHistory.getTracks(),
            callback = { track -> if (clickDebounce()) {
                searchHistory.addTrack(track)
                trackAdapterHistory.notifyDataSetChanged()
                startActivity(audioPlayerIntent.putExtra(INTENT_TRACK_KEY, track))
            }})
        rvTrackListHistory.adapter = trackAdapterHistory
        rvTrackListHistory.layoutManager = LinearLayoutManager(
            this@SearchActivity,
            LinearLayoutManager.VERTICAL,
            false
        )

        if(text.isEmpty()&&searchHistory.getTracks().size!=0){
            historyLL.visibility = View.VISIBLE
        }

        clearEditText.setOnClickListener {
            editTextSearch.setText(EMPTY)
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            trackAdapterHistory.notifyDataSetChanged()
            notFound.visibility = View.GONE
            noInternet.visibility = View.GONE
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(editTextSearch.windowToken, 0)
        }

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Здесь можно добавить любой нужный код перед изменением текста
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (editTextSearch.text.length == 0) {
                    clearEditText.visibility = View.GONE
                } else {
                    clearEditText.visibility = View.VISIBLE
                    searchDebounce()
                }
                // Здесь можно добавить любой нужный код при изменении текста
            }

            override fun afterTextChanged(s: Editable?) {
                text = s.toString()
                if(text.isEmpty()&&searchHistory.getTracks().size!=0){
                    trackAdapterHistory = TrackAdapter(searchHistory.getTracks(),
                        callback = { track -> if (clickDebounce()) {
                            searchHistory.addTrack(track)
                            trackAdapterHistory.notifyDataSetChanged()
                            startActivity(audioPlayerIntent.putExtra(INTENT_TRACK_KEY, track))
                        }})
                    rvTrackListHistory.adapter = trackAdapterHistory
                    rvTrackListHistory.layoutManager = LinearLayoutManager(
                        this@SearchActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    historyLL.visibility = View.VISIBLE
                }else{
                    historyLL.visibility = View.GONE
                }
                // Здесь можно добавить любой нужный код после изменения текста
            }
        })

        val buttonSettings = findViewById<ImageView>(R.id.back_search_image)
        buttonSettings.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        updateButton.setOnClickListener {
            if(clickDebounce()) {
                apiRequest(text)
            }
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            historyLL.visibility = View.GONE
            trackAdapterHistory.notifyDataSetChanged()
        }

        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE&&clickDebounce()) {
                apiRequest(text)
            }
            false
        }

    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, 1000)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, 2000)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(KEY, editTextSearch.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        editTextSearch.setText(savedInstanceState.getString(KEY, EMPTY))
    }

    fun apiRequest(text: String) {
        if(text.isEmpty()){return}
        notFound.visibility = View.GONE
        noInternet.visibility = View.GONE
        historyLL.visibility = View.GONE
        rvTrackList.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        Creator.provideTrackInteractor()
            .search(text, object : TrackInteractor.TrackConsumer {
                override fun consume(foundTracks: List<Track>) {
                    runOnUiThread {
                        if (foundTracks.isNotEmpty()) {
                            trackList.clear()
                            trackList.addAll(foundTracks)
                            trackAdapter.notifyDataSetChanged()
                            notFound.visibility = View.GONE
                            noInternet.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            rvTrackList.visibility = View.VISIBLE
                            rvTrackList.adapter = trackAdapter
                            rvTrackList.layoutManager = LinearLayoutManager(
                                this@SearchActivity,
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                        } else {
                            trackList.clear()
                            noInternet.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            notFound.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onFailure(t: Throwable) {
                    trackList.clear()
                    notFound.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    noInternet.visibility = View.VISIBLE
                }
            })

    }


    companion object {
        private const val KEY = "SEARCH_TEXT"
        const val INTENT_TRACK_KEY = "intent_track"
        private const val EMPTY = ""
    }

}