package net.grandcentrix.backend

import io.ktor.server.application.*

fun Application.module(persistLinks: Boolean) {
    val youtubeManager = if (persistLinks) {
        JsonYouTubeManagerObjectClass.JsonYouTubeManagerObjectInstance
    } else {
        InMemoryYouTubeManagerClass.inMemoryYouTubeManagerInstance
    }

    val playlistManager = youtubeManager as PlayListInterface

    configureRouting(youtubeManager = youtubeManager, playlistManager = playlistManager)
}
