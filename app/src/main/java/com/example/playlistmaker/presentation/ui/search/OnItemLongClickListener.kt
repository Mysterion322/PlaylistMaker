package com.example.playlistmaker.presentation.ui.search

import com.example.playlistmaker.domain.models.Track

fun interface OnItemClickListener {
    fun onItemClick(item: Track)
}

fun interface OnItemLongClickListener {
    fun onItemLongClick(item: Track)
}