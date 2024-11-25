package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.converter.PlaylistDBConverter
import com.example.playlistmaker.data.converter.TrackAtPlaylistDBConverter
import com.example.playlistmaker.data.converter.TrackDBConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.TrackAPI
import com.example.playlistmaker.presentation.SP_PLAYLIST
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<SharedPreferences> {
        androidContext()
            .getSharedPreferences(SP_PLAYLIST, Context.MODE_PRIVATE)
    }

    factory { Gson() }
    single<TrackAPI> {
        Retrofit.Builder().baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(TrackAPI::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient()
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db").build()
    }

    single {
        TrackDBConverter()
    }

    single {
        PlaylistDBConverter()
    }

    single {
        TrackAtPlaylistDBConverter()
    }


}