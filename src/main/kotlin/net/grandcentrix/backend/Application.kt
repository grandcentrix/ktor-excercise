package net.grandcentrix.backend

import io.ktor.server.application.*

fun Application.module() {
    val youtubeManager = getYouTubeManager()
    configureRouting(youtubeManager = youtubeManager)
}

fun getYouTubeManager(): YouTubeManagerInterface {
    JsonYouTubeManagerObjectClass.JsonYouTubeManagerObjectInstance.loadYouTubeLinks()
    return JsonYouTubeManagerObjectClass.JsonYouTubeManagerObjectInstance

}
