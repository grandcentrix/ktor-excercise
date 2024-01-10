package net.grandcentrix.backend

import io.ktor.server.application.*

fun Application.module() {
    JsonYouTubeManager.loadYouTubeLinks()
    configureRouting(youtubeManager = JsonYouTubeManager)
}


