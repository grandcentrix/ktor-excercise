package net.grandcentrix.backend


import io.ktor.server.application.*

fun Application.module(persistLinks: Boolean) {
    val youtubeManager = getYouTubeManager(persistLinks)
    val playlistManager = PlaylistManager()
    configureRouting(youtubeManager = youtubeManager, playlistManager = playlistManager)
}


// very nice the return if!
fun getYouTubeManager(persistLinks: Boolean): YouTubeManagerInterface = if (persistLinks) {
    JsonYouTubeManagerObjectClass.JsonYouTubeManagerObjectInstance.apply {
        loadYouTubeLinks()
    }
} else {
    InMemoryYouTubeManagerClass.inMemoryYouTubeManagerInstance
}
