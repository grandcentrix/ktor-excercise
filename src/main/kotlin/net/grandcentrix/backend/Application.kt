package net.grandcentrix.backend

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import net.grandcentrix.backend.models.Video.VideoManager.Companion.VideoManagerInstance
import net.grandcentrix.backend.plugins.configureRouting
import net.grandcentrix.backend.plugins.configureSecurity
import net.grandcentrix.backend.plugins.configureTemplating

fun main() {
    VideoManagerInstance.getFile()
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSecurity()
    configureTemplating()
    configureRouting(VideoManagerInstance)
}