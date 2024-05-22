package com.example.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackAPI {

    @GET("/search?entity=song")
    fun getTrack(@Query("term") text: String) : Call<ResponseTracks>

}