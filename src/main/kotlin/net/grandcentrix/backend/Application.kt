package net.grandcentrix.backend

import io.ktor.server.application.*

fun Application.module() {
    configureRouting()
    YouTubeManager.loadYouTubeLinks()
}


