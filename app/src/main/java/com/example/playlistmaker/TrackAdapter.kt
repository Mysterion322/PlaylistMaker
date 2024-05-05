package com.example.playlistmaker

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.lang.IllegalStateException

class TrackAdapter: RecyclerView.Adapter<ViewHolder>() {

    var items: List<Track> = emptyList()
        set(value) {
            val oldItems = field
            val newItems = value.toMutableList()

            val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return oldItems.size
                }

                override fun getNewListSize(): Int {
                    return newItems.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return oldItems[oldItemPosition].hashCode() == newItems[newItemPosition].hashCode()
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return oldItems[oldItemPosition] == newItems[newItemPosition]
                }

            })
            field = newItems
            diffResult.dispatchUpdatesTo(this)
//            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.track_card, parent, false)
        return TrackHolder(itemView)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as TrackHolder).bind(items[position])
    }

}