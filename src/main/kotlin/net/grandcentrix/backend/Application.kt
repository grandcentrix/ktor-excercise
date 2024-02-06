package net.grandcentrix.backend

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import net.grandcentrix.backend.models.StorageManagerFile.Companion.StorageManagerFileInstance
import net.grandcentrix.backend.models.StorageManagerMemory.Companion.StorageManagerMemoryInstance
import net.grandcentrix.backend.models.VideoManager.Companion.VideoManagerInstance
import net.grandcentrix.backend.plugins.configureRouting
import net.grandcentrix.backend.plugins.configureSecurity
import net.grandcentrix.backend.plugins.configureTemplating

fun main() {
    val saveVideos = true

    if (saveVideos) {
        val videosJson = StorageManagerFileInstance.listVideos()
        if (videosJson.isNotEmpty()) {
            StorageManagerMemoryInstance.updateStorage(videosJson)
            VideoManagerInstance.storeIn = StorageManagerFileInstance
            VideoManagerInstance.loadVideosToType(videosJson)
        }
    }

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSecurity()
    configureTemplating()
    configureRouting(VideoManagerInstance)
}