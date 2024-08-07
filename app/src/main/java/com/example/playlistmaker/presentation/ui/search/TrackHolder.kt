package com.example.playlistmaker.presentation.ui.search

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val ivTrack: ImageView = itemView.findViewById(R.id.iv_track)
    private val tvTrackName: TextView = itemView.findViewById(R.id.tv_track_name)
    private val tvTrackArtist: TextView = itemView.findViewById(R.id.tv_track_artist)
  // private val ivTermsOfUse: ImageView = itemView.findViewById(R.id.iv_terms_of_use)

    fun bind(item: Track) {
        tvTrackName.text = item.trackName
        tvTrackArtist.text = item.artistName+" • "+ item.trackTime
        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.api_image_radius)))
            .into(ivTrack)

    }

}