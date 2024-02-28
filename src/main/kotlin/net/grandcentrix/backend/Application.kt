package net.grandcentrix.backend

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import net.grandcentrix.backend.models.FormManager.Companion.FormManagerInstance
import net.grandcentrix.backend.models.StorageManagerFile.Companion.StorageManagerFileInstance
import net.grandcentrix.backend.models.StorageManagerMemory.Companion.StorageManagerMemoryInstance
import net.grandcentrix.backend.models.StorageManagerTypesFile.Companion.StorageManagerTypesFileInstance
import net.grandcentrix.backend.models.VideoManager.Companion.VideoManagerInstance
import net.grandcentrix.backend.models.VideoType
import net.grandcentrix.backend.plugins.configureRouting
import net.grandcentrix.backend.plugins.configureSecurity
import net.grandcentrix.backend.plugins.configureTemplating

fun main() {

    storageSetup()

    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::module)
        .start(wait = true)
}

fun saveVideos(): Boolean = true

fun storageSetup() {
    saveVideos()

    // populates video types file
    if (StorageManagerTypesFileInstance.getContent().isEmpty()) {
        val videoTypeNames = VideoType.entries.map { it.name }
        StorageManagerTypesFileInstance.setContent(videoTypeNames)
    }

    if (saveVideos()) {
        val videosJson = StorageManagerFileInstance.getContent()
        if (videosJson.isNotEmpty()) {
            StorageManagerMemoryInstance.setContent(videosJson)
            VideoManagerInstance.defineStorage(StorageManagerFileInstance)
            VideoManagerInstance.loadVideosToTypeList(videosJson)
        }
    }
}

fun Application.module() {
    configureSecurity()
    configureTemplating()
    configureRouting(VideoManagerInstance, FormManagerInstance)
}