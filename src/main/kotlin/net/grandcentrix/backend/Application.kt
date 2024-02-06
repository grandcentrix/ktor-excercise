package net.grandcentrix.backend


import io.ktor.server.application.*

fun Application.module(persistLinks: Boolean) {
    val youtubeManager = getYouTubeManager(persistLinks)
    val playlistManager = PlaylistManager()
    configureRouting(youtubeManager = youtubeManager, playlistManager = playlistManager)
}



fun getYouTubeManager(persistLinks: Boolean): YouTubeManagerInterface {
    return if (persistLinks) {
        val jsonManager = JsonYouTubeManagerObjectClass.JsonYouTubeManagerObjectInstance
        jsonManager.loadYouTubeLinks()
        jsonManager
    } else {
        InMemoryYouTubeManagerClass.inMemoryYouTubeManagerInstance
    }
}
