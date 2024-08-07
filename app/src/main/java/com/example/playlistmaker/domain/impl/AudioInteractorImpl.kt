package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.AudioInteractor
import com.example.playlistmaker.domain.api.AudioRepository

class AudioInteractorImpl (private val audioRepository: AudioRepository): AudioInteractor {
    override fun pauseAudio(){
        audioRepository.pausePlayer()
    }
}