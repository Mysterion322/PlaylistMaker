package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.ItunesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackAPI {
    @GET("/search?entity=song")
    suspend fun search(@Query("term") text: String): ItunesResponse
}