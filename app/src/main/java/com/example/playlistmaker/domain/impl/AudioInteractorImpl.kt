package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.AudioInteractor
import com.example.playlistmaker.domain.api.AudioRepository
import com.example.playlistmaker.presentation.ui.audio_player.PlayingState

class AudioInteractorImpl(private val audioRepository: AudioRepository) : AudioInteractor {

    override fun pauseAudioPlayer() {
        audioRepository.pausePlayer()
    }

    override fun startAudioPlayer() {
        audioRepository.startPlayer()
    }

    override fun releaseAudio() {
        audioRepository.release()
    }

    override fun getCurrentAudioPosition(): Int {
        return audioRepository.getCurrentPosition()
    }

    override fun getAudioState(): PlayingState {
        return audioRepository.getState()
    }


}