package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun search(expression: String, consumer: TrackInteractor.TrackConsumer) {
        executor.execute {
            try {
                consumer.consume(repository.searchTracks(expression))
            } catch (throwable: Throwable) {
                consumer.onFailure(throwable)
            }

        }
    }
}