package com.example.playlistmaker.presentation.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackAdapter(
    private val onItemClickListener: OnItemClickListener,
    private val onItemLongClickListener: OnItemLongClickListener? = null
) : RecyclerView.Adapter<TrackHolder>() {
    var items = mutableListOf<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.track_card, parent, false)
        return TrackHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        holder.bind(items[position])
        holder.itemView.setOnClickListener { onItemClickListener.onItemClick(items[position]) }
        if (onItemLongClickListener != null)
            holder.itemView.setOnLongClickListener {
                onItemLongClickListener.onItemLongClick(items[position])
                true
            }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun clearItems() {
        val oldSize = itemCount
        items.clear()
        notifyItemRangeRemoved(0, oldSize)
    }

}