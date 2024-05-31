package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private lateinit var editTextSearch: EditText
    private lateinit var notFound: LinearLayout
    private lateinit var noInternet: LinearLayout
    private lateinit var historyLL: LinearLayout
    private val searchHistory = SearchHistory(MainActivity.sharedPrefs)
    private var text: String = EMPTY
    private val trackList = mutableListOf<Track>()
    private val trackAdapter = TrackAdapter(trackList,
        callback = { track -> run { searchHistory.addTrack(track) }})
    private lateinit var trackAdapterHistory: TrackAdapter
    private lateinit var rvTrackList: RecyclerView
    private lateinit var rvTrackListHistory: RecyclerView


        private val baseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(TrackAPI::class.java)


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
        val updateButton = findViewById<Button>(R.id.update_button)
        val clearHistoryButton = findViewById<Button>(R.id.clear_history_button)
        searchHistory.getTracks()
        trackAdapterHistory = TrackAdapter(searchHistory.historyList,
            callback = { track -> run { searchHistory.addTrack(track) } })

        rvTrackListHistory.adapter = trackAdapterHistory
        rvTrackListHistory.layoutManager = LinearLayoutManager(
            this@SearchActivity,
                LinearLayoutManager.VERTICAL,
                false
            )

        if(searchHistory.historyList.size!=0&&text.isEmpty()){
            historyLL.visibility = View.VISIBLE
        }

        editTextSearch.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)

        editTextSearch.setOnTouchListener(OnTouchListener { v, event ->

            val drawableRight = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= editTextSearch.getRight() - editTextSearch.getCompoundDrawables()
                        .get(drawableRight).getBounds().width()
                ) {
                    editTextSearch.setText(EMPTY)
                    editTextSearch.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.search_small_image,
                        0,
                        R.drawable.search_image_layout,
                        0
                    )
                    trackList.clear()
                    notFound.visibility = View.GONE
                    noInternet.visibility = View.GONE
                    trackAdapter.notifyDataSetChanged()

                    val view: View? = this.currentFocus

                    if (view != null) {
                        val inputMethodManager =
                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0)
                    }

                    editTextSearch.setCursorVisible(false)
                    return@OnTouchListener true
                }
            }
            editTextSearch.setCursorVisible(true)
            false
        })

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Здесь можно добавить любой нужный код перед изменением текста
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (editTextSearch.text.length == 0) {
                    editTextSearch.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.search_small_image, 0,
                        R.drawable.search_image_layout, 0
                    )
                } else {
                    editTextSearch.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.search_small_image, 0,
                        R.drawable.clear_small_image, 0
                    )
                }
                // Здесь можно добавить любой нужный код при изменении текста
            }

            override fun afterTextChanged(s: Editable?) {
                text = s.toString()
                if(searchHistory.historyList.size!=0&&text.isEmpty()){
                    trackAdapterHistory.notifyDataSetChanged()
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
            noInternet.visibility = View.GONE
            apiRequest(text)
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.historyList.clear()
            historyLL.visibility = View.GONE
            trackAdapterHistory.notifyDataSetChanged()
        }

        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                apiRequest(text)
                true
            }
            false
        }

    }

    override fun onStop() {
        super.onStop()
        searchHistory.putTracks()
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
        iTunesService.getTrack(text).enqueue(object : Callback<ResponseTracks> {
            override fun onResponse(call: Call<ResponseTracks>, response: Response<ResponseTracks>) {

                val tracksFromResp = response.body()?.results

                if (response.isSuccessful && tracksFromResp != null) {
                    if (tracksFromResp.isEmpty()) {
                        trackList.clear()
                        noInternet.visibility = View.GONE
                        notFound.visibility = View.VISIBLE
                    } else {
                        trackList.clear()
                        trackList.addAll(tracksFromResp.toMutableList())
                        notFound.visibility = View.GONE
                        noInternet.visibility = View.GONE

                        rvTrackList.adapter = trackAdapter
                        rvTrackList.layoutManager = LinearLayoutManager(
                                this@SearchActivity,
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                    }
                }

            }

            override fun onFailure(p0: Call<ResponseTracks>, p1: Throwable) {
                trackList.clear()
                notFound.visibility = View.GONE
                noInternet.visibility = View.VISIBLE

            }
        })
    }


    companion object {
        private const val KEY = "SEARCH_TEXT"
        private const val EMPTY = ""
    }

}