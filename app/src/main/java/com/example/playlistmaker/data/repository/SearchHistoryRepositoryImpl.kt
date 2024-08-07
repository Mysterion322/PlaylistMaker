package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl (private val sharedPref: SharedPreferences) :
    SearchHistoryRepository {

    private val maxLength = 10
    private val historyKeySP = "tracks_history"

    var historyList = mutableListOf<Track>()

    override fun getTracks(): MutableList<Track>{
        val strFromSP = sharedPref.getString(historyKeySP, null)
        historyList = listFromJson(strFromSP)
        return historyList
    }

    private fun putTracks(){
        val listToStr = Gson().toJson(historyList)
        sharedPref.edit().putString(historyKeySP, listToStr).apply()
    }

    private fun listFromJson(json: String?): MutableList<Track>{
        val listType = object : TypeToken<MutableList<Track>>() {}.type
        return Gson().fromJson(json, listType) ?: mutableListOf()
    }

    override fun addTrack(track: Track){
        if (historyList.contains(track)) {
            historyList.remove(track)
        }else if (historyList.size >= maxLength){
            historyList.removeAt(maxLength-1)
        }
        historyList.add(0, track)
        putTracks()
    }

    override fun clearHistory() {
        historyList.clear()
        sharedPref.edit().remove(historyKeySP).apply()
    }


}