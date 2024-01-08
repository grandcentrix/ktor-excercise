package net.grandcentrix.backend

import io.ktor.server.application.*

fun Application.module() {
    configureRouting()
    loadYouTubeLinks()
}

fun getRandomYouTubeVideoUrl(): String {
    if (youtubeLinks.isEmpty()) {
        return "https://www.youtube.com/"
    }
    val randomIndex = (0 until youtubeLinks.size).random()
    val videoInfo = youtubeLinks[randomIndex]
    val videoId = videoInfo.videoId

    return "https://www.youtube.com/embed/$videoId"
}

