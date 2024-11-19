package com.example.playlistmaker.domain.models

data class Playlist(
    val id: Int,
    val name: String,
    val description: String,
    val imagePath: String?,
    val tracks: ArrayList<String>,
)
