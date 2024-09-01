package com.example.playlistmaker.presentation.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackAdapter(
    private val items: MutableList<Track>,
    private val callback: (Track) -> Unit
) : RecyclerView.Adapter<TrackHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.track_card, parent, false)
        return TrackHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            callback(items[position])
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}