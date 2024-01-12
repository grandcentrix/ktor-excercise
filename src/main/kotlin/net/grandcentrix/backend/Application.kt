package net.grandcentrix.backend

import io.ktor.server.application.*

fun Application.module() {
    val youtubeManager = getYouTubeManager()
    youtubeManager.loadYouTubeLinks()
    configureRouting(youtubeManager = youtubeManager)
}

fun getYouTubeManager(): YouTubeManagerInterface {
    return InMemoryYouTubeManager

}
