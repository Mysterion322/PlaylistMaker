package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
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


class SearchActivity : AppCompatActivity() {


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val editTextSearch = findViewById<EditText>(R.id.search_edit_text)

        editTextSearch.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)

        editTextSearch.setOnTouchListener(OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= editTextSearch.getRight() - editTextSearch.getCompoundDrawables()
                        .get(DRAWABLE_RIGHT).getBounds().width()
                ) {
                    editTextSearch.setText("")
                    editTextSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search_small_image,0, R.drawable.search_image_layout, 0)

                    val view: View? = this.currentFocus

                    // on below line checking if view is not null.
                    if (view != null) {
                        // on below line we are creating a variable
                        // for input manager and initializing it.
                        val inputMethodManager =
                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

                        // on below line hiding our keyboard.
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
              //  editTextSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search_small_image,0, R.drawable.search_image_layout, 0)
                // Здесь можно добавить любой нужный код после изменения текста
            }
        })

        val buttonSettings = findViewById<ImageView>(R.id.back_search_image)

        buttonSettings.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

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

}