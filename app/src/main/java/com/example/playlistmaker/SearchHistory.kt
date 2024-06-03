package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory (private val sharedPref: SharedPreferences) {

    private val gson = Gson()
    private val maxLength = 10
    private val historyKeySP = "tracks_history"

    var historyList = mutableListOf<Track>()

    fun getTracks(){
        val strFromSP = sharedPref.getString(historyKeySP, null)
        historyList = listFromJson(strFromSP)
    }

    fun putTracks(){
        val listToStr = gson.toJson(historyList)
        sharedPref.edit().putString(historyKeySP, listToStr).apply()
    }

    private fun listFromJson(json: String?): MutableList<Track>{
        val listType = object : TypeToken<MutableList<Track>>() {}.type
        return gson.fromJson(json, listType) ?: mutableListOf()
    }

    fun addTrack(track: Track){
        if (historyList.contains(track)) {
            historyList.remove(track)
        }
        if (historyList.size >= maxLength){
            historyList.removeAt(maxLength-1)
        }
        historyList.add(0, track)
    }

}