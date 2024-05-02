package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    val ivTrack: ImageView = itemView.findViewById(R.id.iv_track)
    val tvTrackName: TextView = itemView.findViewById(R.id.tv_track_name)
    val tvTrackArtist: TextView = itemView.findViewById(R.id.tv_track_artist)
  //  val ivTermsOfUse: ImageView = itemView.findViewById(R.id.iv_terms_of_use)

    fun bind(item: Track) {
        tvTrackName.text = item.trackName
        tvTrackArtist.text = item.artistName+" • "+item.trackTime
        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.musicicon2)
            .centerCrop()
            .transform(RoundedCorners(10))
            .into(ivTrack)

    }

}