package com.example.playlistmaker

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.lang.IllegalStateException

class TrackAdapter(
    private val items: MutableList<Track>
   ) : RecyclerView.Adapter<TrackHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.track_card, parent, false)
        return TrackHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}