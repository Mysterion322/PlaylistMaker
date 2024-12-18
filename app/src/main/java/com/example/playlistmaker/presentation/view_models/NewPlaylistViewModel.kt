package com.example.playlistmaker.presentation.view_models

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.getDefaultCacheImagePath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class NewPlaylistViewModel(
    private val interactor: PlaylistInteractor,
    private val application: Application,
) : ViewModel() {
    private val playlist = MutableLiveData<Playlist?>()
    fun observePlaylist(): LiveData<Playlist?> = playlist

    fun createPlaylist(
        playlistName: String,
        playlistDescription: String,
        imageUri: Uri,
        bitmap: Bitmap,
    ): Long {
        var result = 0L
        var playlistImage: String? = null
        if (imageUri != Uri.EMPTY)
            playlistImage = "${UUID.randomUUID()}.png"
        viewModelScope.launch(Dispatchers.IO) {
            result = interactor.createPlaylist(playlistName, playlistDescription, playlistImage)
            if (result > 0 && imageUri != Uri.EMPTY) {
                saveImageToPrivateStorage(
                    bitmap,
                    playlistImage!!
                )
            }
        }
        return result
    }

    fun updatePlaylist(
        playlistName: String,
        playlistDescription: String,
        bitmap: Bitmap,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val playlistImage = "${UUID.randomUUID()}.png"
            interactor.updatePlaylist(
                playlist.value!!.copy(
                    name = playlistName,
                    description = playlistDescription,
                    imagePath = playlistImage
                )
            )
            saveImageToPrivateStorage(bitmap, playlistImage)
        }
    }

    fun getPlaylist(playlistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.getPlaylistById(playlistId).collect {
                playlist.postValue(it)
            }
        }
    }


    private fun saveImageToPrivateStorage(bitmap: Bitmap, fileName: String): Boolean {
        if (fileName.isEmpty()) return false
        val filePath =
            getDefaultCacheImagePath(application)
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, fileName)
        val outputStream = FileOutputStream(file)
        return bitmap
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }
}