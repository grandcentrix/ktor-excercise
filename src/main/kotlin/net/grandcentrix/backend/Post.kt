package net.grandcentrix.backend



import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.html.*
import kotlinx.coroutines.delay
import kotlinx.html.*
import java.net.URL



fun Application.configurePostRoutes(youtubeManager: YouTubeManagerInterface, playlistManager: PlayListInterface) {
    routing {
        addVideo(youtubeManager)
        addVideos(youtubeManager)
        addVideoToPlaylist(youtubeManager)
        deleteVideoByNumber(youtubeManager)
        renameVideoByNumber(youtubeManager)
        removeVideo(playlistManager)
        userPlaylistPage(playlistManager)
        switchPlaylist(playlistManager)
        createPlaylist(playlistManager)
        deletePlaylist(playlistManager)
        renamePlaylist(playlistManager)
    }
}


private fun Routing.deletePlaylist(playlistManager: PlayListInterface) {
    post("/deletePlaylist") {
        val parameters = call.receiveParameters()
        val playlistNameToDelete = parameters["playlistNameToDelete"]

        if (playlistNameToDelete.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Invalid playlist name")
        } else {
            try {
                // Delete the playlist by name
                playlistManager.deletePlaylist(playlistNameToDelete)
                call.respondRedirect("/")
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Error deleting playlist")
            }
        }
    }
}

private fun Routing.createPlaylist(playlistManager: PlayListInterface) {
    post("/createPlaylist") {
        val parameters = call.receiveParameters()
        val playlistName = parameters["playlistName"] ?: return@post // Ensure playlist name is provided

        try {
            // Attempt to create the playlist with the provided custom name
            playlistManager.createPlaylist(playlistName)
            // No need to sleep here
            call.respondRedirect("/")
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "Error creating playlist")
        }
    }
}

private fun Routing.switchPlaylist(playlistManager: PlayListInterface) {
    post("/switchPlaylist") {
        val parameters = call.receiveParameters()
        val playlistName = parameters["playlistName"] ?: return@post
        playlistManager.switchPlaylist(playlistName)
        call.respondRedirect("/")
    }
}

private fun Routing.userPlaylistPage(playlistManager: PlayListInterface) {
    get("/userPlaylistPage") {
        val currentPlaylist = playlistManager.getCurrentPlaylist()
        playlistManager.loadPlaylists()

        if (currentPlaylist == null) {
            call.respond(HttpStatusCode.BadRequest, "No playlist selected")
        } else {
            call.respondHtml {
                body {
                    currentPlaylist.videos.forEach { videoInfo ->
                        div {
                            iframe {
                                width = "560"
                                height = "315"
                                src = "https://www.youtube.com/embed/${videoInfo.videoId}"
                                attributes["frameborder"] = "0"
                                attributes["fullscreen"] = "true"
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun Routing.removeVideo(playlistManager: PlayListInterface) {
    post("/removeVideo") {
        val parameters = call.receiveParameters()
        val videoIndexToRemove = parameters["videoIndexToRemove"]?.toIntOrNull()

        val currentPlaylist = playlistManager.getCurrentPlaylist()

        if (currentPlaylist == null || videoIndexToRemove == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid parameters or no current playlist")
        } else {
            if (videoIndexToRemove < 0 || videoIndexToRemove >= currentPlaylist.videos.size) {
                call.respond(HttpStatusCode.BadRequest, "Invalid video index: $videoIndexToRemove")
            } else {
                val removedVideo = currentPlaylist.videos.removeAt(videoIndexToRemove)
                playlistManager.savePlaylists()
                call.respondRedirect("/")
            }
        }
    }
}

private fun Routing.renameVideoByNumber(youtubeManager: YouTubeManagerInterface) {
    post("/renameVideoByNumber") {
        val parameters = call.receiveParameters()
        val videoNumberToRename = parameters["videoNumberToRename"]?.toIntOrNull()
        val newCustomName = parameters["newCustomName"]

        if (videoNumberToRename == null || videoNumberToRename <= 0 ||
            videoNumberToRename > youtubeManager.getYoutubeLinks().size || newCustomName.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Invalid video number or new custom name")
        } else {
            val indexToRename = videoNumberToRename - 1
            val videoInfo = youtubeManager.getYoutubeLinks()[indexToRename]
            videoInfo.customName = newCustomName
            call.respondRedirect("/")
        }
    }
}

private fun Routing.deleteVideoByNumber(youtubeManager: YouTubeManagerInterface) {
    post("/deleteVideoByNumber") {
        val parameters = call.receiveParameters()
        val videoNumberToDelete = parameters["videoNumberToDelete"]?.toIntOrNull()

        if (videoNumberToDelete == null || videoNumberToDelete <= 0 ||
            videoNumberToDelete > youtubeManager.getYoutubeLinks().size) {
            call.respond(HttpStatusCode.BadRequest, "Invalid video number")
        } else {
            val indexToDelete = videoNumberToDelete - 1
            youtubeManager.removeVideoByNumber(indexToDelete)
            call.respondRedirect("/")
        }
    }
}


private fun Routing.addVideos(youtubeManager: YouTubeManagerInterface) {
    post("/addVideos") {
        val parameters = call.receiveParameters()
        val newVideoUrl = parameters["newVideoUrl"]
        val customName = parameters["customName"] ?: ""

        val validationResult = youtubeManager.validateVideoUrl(newVideoUrl)
        if (validationResult != null) {
            val (status, message) = validationResult
            call.respond(status, message)
        } else {
            val videoIdPattern = Regex("[?&]v=([^&]+)")
            val matchResult = videoIdPattern.find(newVideoUrl ?: "")

            val videoId = matchResult?.groupValues?.getOrNull(1)
            try {
                if (videoId != null) {
                    youtubeManager.addVideos(videoId, customName)
                }
                youtubeManager.saveYouTubeLinksJson()
                call.respondRedirect("/")
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Error adding video")
            }
        }
    }
}


private fun Routing.addVideo(youtubeManager:YouTubeManagerInterface) {
    post("/addVideo") {
        val parameters = call.receiveParameters()
        val newVideoUrl = parameters["newVideoUrl"]
        val customName = parameters["customName"] ?: ""
        val playlistName = parameters["playlistName"] ?: ""

        val validationResult = youtubeManager.validateVideoUrl(newVideoUrl)
        if (validationResult != null) {
            val (status, message) = validationResult
            call.respond(status, message)
        } else {
            val videoIdPattern = Regex("[?&]v=([^&]+)")
            val matchResult = videoIdPattern.find(newVideoUrl ?: "")

            val videoId = matchResult?.groupValues?.getOrNull(1)
            try {
                // Add video to playlist (if provided) or default playlist
                if (videoId != null) {
                    youtubeManager.addVideoToPlaylist(videoId, customName, playlistName)
                }
                call.respondRedirect("/")
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Error adding video to playlist")
            }
        }
    }
}


private fun Routing.addVideoToPlaylist(youtubeManager: YouTubeManagerInterface) {
    post("/addVideoToPlaylist") {
        val parameters = call.receiveParameters()
        val videoId = parameters["videoId"]
        val customName = parameters["customName"]
        val playlistName = parameters["playlistName"]


        if (videoId != null && playlistName != null && playlistName.isNotBlank()) {
            try {
                youtubeManager.addVideoToPlaylist(videoId, customName, playlistName)
                call.respondRedirect("/")
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, "Error adding video to playlist: ${e.message}")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Internal server error: ${e.message}")
                call.application.log.error("Error adding video to playlist", e)
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid request parameters")
        }
    }
}

private fun Routing.renamePlaylist(playlistManager: PlayListInterface) {
    post("/renamePlaylist") {
        val parameters = call.receiveParameters()
        val oldName = parameters["playlistNameToRename"]
        val newName = parameters["newPlaylistName"]

        if (oldName.isNullOrBlank() || newName.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Invalid playlist names")
        } else {
            try {
                playlistManager.renamePlaylist(oldName, newName)
                println("Old Name: $oldName, New Name: $newName")
                call.respondRedirect("/")
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Error renaming playlist")
            }
        }
    }
}


