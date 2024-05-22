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
    private lateinit var notFoundIV: ImageView
    private lateinit var noInternetIV: ImageView
    private lateinit var noInternet: LinearLayout
    private var text: String = EMPTY
    private val trackList = mutableListOf<Track>()
    private val trackAdapter = TrackAdapter(trackList)
    private lateinit var rvTrackList: RecyclerView

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
        editTextSearch = findViewById<EditText>(R.id.search_edit_text)
        notFound = findViewById<LinearLayout>(R.id.ll_not_found)
        noInternet = findViewById<LinearLayout>(R.id.ll_no_internet)
        notFoundIV = findViewById<ImageView>(R.id.iv_not_found)
        noInternetIV = findViewById<ImageView>(R.id.iv_no_internet)
        val updateButton = findViewById<Button>(R.id.update_button)

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
                    text = EMPTY
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
                        R.drawable.search_small_image,
                        0,
                        R.drawable.search_image_layout,
                        0
                    )
                } else {
                    editTextSearch.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.search_small_image,
                        0,
                        R.drawable.clear_small_image,
                        0
                    )
                }
                // Здесь можно добавить любой нужный код при изменении текста
            }

            override fun afterTextChanged(s: Editable?) {
                text = s.toString()
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

        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                apiRequest(text)
                true
            }
            false
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val editTextSearch = findViewById<EditText>(R.id.search_edit_text)
        outState.putString(KEY, editTextSearch.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val editTextSearch = findViewById<EditText>(R.id.search_edit_text)
        editTextSearch.setText(savedInstanceState.getString(KEY, EMPTY))
    }

    fun apiRequest(text: String) {
        iTunesService.getTrack(text).enqueue(object : Callback<ResponseTracks> {
            override fun onResponse(
                call: Call<ResponseTracks>,
                response: Response<ResponseTracks>
            ) {

                val tracksFromResp = response.body()?.results

                if (response.isSuccessful && tracksFromResp != null) {
                    if (tracksFromResp.isEmpty()) {
                        if(resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES){
                            notFoundIV.setImageResource(R.drawable.notfounddark)
                        }else{
                            notFoundIV.setImageResource(R.drawable.notfound)
                        }
                        noInternet.visibility = View.GONE
                        notFound.visibility = View.VISIBLE
                        trackList.clear()
                    } else {
                        trackList.clear()
                        trackList.addAll(tracksFromResp.toMutableList())
                        notFound.visibility = View.GONE
                        noInternet.visibility = View.GONE

                        rvTrackList.adapter = trackAdapter
                        rvTrackList.layoutManager =
                            LinearLayoutManager(
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
                if(resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES){
                    noInternetIV.setImageResource(R.drawable.nointernetdark)
                }else{
                    noInternetIV.setImageResource(R.drawable.nointernet)
                }
                noInternet.visibility = View.VISIBLE

            }
        })
    }


    companion object {
        private const val KEY = "SEARCH_TEXT"
        private const val EMPTY = ""
        var switchDarkBoolean = false
    }

}