package net.grandcentrix.backend

import io.ktor.server.application.*

fun Application.module() {
    val youtubeManager = getYouTubeManager()
    configureRouting(youtubeManager = youtubeManager)
}

fun getYouTubeManager(): YouTubeManagerInterface {
    return if (YouTubeManagerConfig.useJsonManager) {
        JsonYouTubeManagerObjectClass.JsonYouTubeManagerObjectInstance.loadYouTubeLinks()
        JsonYouTubeManagerObjectClass.JsonYouTubeManagerObjectInstance
    } else {
        InMemoryYouTubeManagerClass.inMemoryYouTubeManagerInstance
    }
}
