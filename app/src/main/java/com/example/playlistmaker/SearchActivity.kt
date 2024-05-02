package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class SearchActivity : AppCompatActivity() {


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val editTextSearch = findViewById<EditText>(R.id.search_edit_text)

        editTextSearch.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)

        editTextSearch.setOnTouchListener(OnTouchListener { v, event ->

            val drawableRight = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= editTextSearch.getRight() - editTextSearch.getCompoundDrawables()
                        .get(drawableRight).getBounds().width()
                ) {
                    editTextSearch.setText("")
                    editTextSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search_small_image,0, R.drawable.search_image_layout, 0)

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

                if(editTextSearch.text.length==0){
                    editTextSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search_small_image,0, R.drawable.search_image_layout, 0)
                }else{
                    editTextSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search_small_image,0, R.drawable.clear_small_image, 0)
                }
                // Здесь можно добавить любой нужный код при изменении текста
            }

            override fun afterTextChanged(s: Editable?) {
                // Здесь можно добавить любой нужный код после изменения текста
            }
        })

        val buttonSettings = findViewById<ImageView>(R.id.back_search_image)

        buttonSettings.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        val rvTrackList = findViewById<RecyclerView>(R.id.rv_track_list)
        rvTrackList.apply {
            adapter = trackAdapter
            layoutManager =
                LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
        }

        trackAdapter.items = trackList


    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val editTextSearch = findViewById<EditText>(R.id.search_edit_text)
        outState.putString("SEARCH_TEXT", editTextSearch.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val editTextSearch = findViewById<EditText>(R.id.search_edit_text)
        editTextSearch.setText(savedInstanceState.getString("SEARCH_TEXT", ""))
    }



    val trackList = arrayListOf<Track>(
        Track(
            "Smells Like Teen Spirit",
            "Nirvana",
            "5:01",
            "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
        ),Track(
            "Billie Jean",
            "Michael Jackson",
            "4:35",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
        ),Track(
            "Stayin' Alive",
            "Bee Gees",
            "4:10",
            "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
        ),Track(
            "Whole Lotta Love",
            "Led Zeppelin",
            "5:33",
            "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
        ),Track(
            "Sweet Child O'Mine",
            "Guns N' Roses",
            "5:03",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
        )
    )

    val trackAdapter = TrackAdapter()





}