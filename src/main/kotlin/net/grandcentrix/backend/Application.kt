package net.grandcentrix.backend

import io.ktor.server.application.*

fun Application.module() {
    val persistLinks = readLine()?.toBoolean() ?: false
    val youtubeManager = getYouTubeManager(persistLinks)
    configureRouting(youtubeManager = youtubeManager)
}

fun getYouTubeManager(persistLinks: Boolean): YouTubeManagerInterface {
    return if (persistLinks) {
        val jsonManager = JsonYouTubeManagerObjectClass.JsonYouTubeManagerObjectInstance
        jsonManager.loadYouTubeLinks()
        jsonManager
    } else {
        InMemoryYouTubeManagerClass.inMemoryYouTubeManagerInstance
    }
}
