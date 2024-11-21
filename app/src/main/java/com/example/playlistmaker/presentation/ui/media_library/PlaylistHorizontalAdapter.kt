package com.example.playlistmaker.presentation.ui.media_library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist

class PlaylistHorizontalAdapter(
    private val playlists: List<Playlist>,
    private val onItemClickListener: OnItemClickListener,
) :
    RecyclerView.Adapter<PlaylistHorizontalViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PlaylistHorizontalViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.playlist_horizontal_item, parent, false)
        return PlaylistHorizontalViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistHorizontalViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { onItemClickListener.onItemClick(playlists[position]) }
    }
}

fun interface OnItemClickListener {
    fun onItemClick(item: Playlist)
}