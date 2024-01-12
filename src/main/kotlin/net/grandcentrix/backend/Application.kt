package net.grandcentrix.backend

import io.ktor.server.application.*

fun Application.module() {
    val youtubeManager: YouTubeManagerInterface = getYouTubeManager()
    configureRouting(youtubeManager = youtubeManager)
}

fun getYouTubeManager(): YouTubeManagerInterface {
    JsonYouTubeManagerObject.loadYouTubeLinks()
    return InMemoryYouTubeManagerClass.InMemoryYouTubeManagerInstance

}
