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


fun Application.configurePostRoutes(youtubeManager: YouTubeManagerInterface, playlistManager: PlaylistManager) {
    routing {
        post("/addVideoToPlaylist") {
            val parameters = call.receiveParameters()
            val videoId = parameters["videoId"]
            val customName = parameters["customName"]
            val playlistName = parameters["playlistName"]

            println("Received parameters: videoId=$videoId, customName=$customName, playlistName=$playlistName")

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




        post("/addVideo") {
            val parameters = call.receiveParameters()
            val newVideoUrl = parameters["newVideoUrl"]
            val customName = parameters["customName"] ?: ""
            val playlistName = parameters["playlistName"] ?: "" // Extract playlistName parameter

            if (!newVideoUrl.isNullOrBlank()) {
                val url = URL(newVideoUrl)
                val host = url.host

                if (host == "www.youtube.com" || host == "youtube.com") {
                    val videoId = url.query?.split("v=")?.get(1)?.split("&")?.get(0)

                    if (!videoId.isNullOrBlank()) {
                        try {
                            youtubeManager.saveYouTubeLinks()
                            call.respondRedirect("/")
                        } catch (e: IllegalArgumentException) {
                            call.respond(HttpStatusCode.BadRequest, e.message ?: "Error adding video to playlist")
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid YouTube URL: Video ID not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid YouTube URL: Host is not supported")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "URL is required")
            }
        }

        post("/addVideos") {
            val parameters = call.receiveParameters()
            val newVideoUrl = parameters["newVideoUrl"]
            val customName = parameters["customName"] ?: ""

            if (!newVideoUrl.isNullOrBlank()) {
                val url = URL(newVideoUrl)
                val host = url.host

                if (host == "www.youtube.com" || host == "youtube.com") {
                    val videoId = url.query?.split("v=")?.get(1)?.split("&")?.get(0)

                    if (!videoId.isNullOrBlank()) {
                        try {
                            youtubeManager.addVideos(videoId, customName)
                            youtubeManager.saveYouTubeLinks()
                            call.respondRedirect("/")
                        } catch (e: IllegalArgumentException) {
                            call.respond(HttpStatusCode.BadRequest, e.message ?: "Error adding video")
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid YouTube URL: Video ID not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid YouTube URL: Host is not supported")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "URL is required")
            }
        }




        post("/deleteVideoByNumber") {
            val parameters = call.receiveParameters()
            val videoNumberToDelete = parameters["videoNumberToDelete"]?.toIntOrNull()

            if (videoNumberToDelete != null && videoNumberToDelete > 0 && videoNumberToDelete <= youtubeManager.getYoutubeLinks().size) {
                val indexToDelete = videoNumberToDelete - 1
                youtubeManager.removeVideoByNumber(indexToDelete)
                call.respondRedirect("/")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid video number")
            }
        }


        post("/renameVideoByNumber") {
            val parameters = call.receiveParameters()
            val videoNumberToRename = parameters["videoNumberToRename"]?.toIntOrNull()
            val newCustomName = parameters["newCustomName"]

            if (videoNumberToRename != null && videoNumberToRename > 0 && videoNumberToRename <= youtubeManager.getYoutubeLinks().size && !newCustomName.isNullOrBlank()) {
                val indexToRename = videoNumberToRename - 1
                val videoInfo = youtubeManager.getYoutubeLinks()[indexToRename]
                videoInfo.customName = newCustomName
                call.respondRedirect("/")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid video number or new custom name")
            }
        }



        post("/removeVideo") {
            val parameters = call.receiveParameters()
            val videoIndexToRemove = parameters["videoIndexToRemove"]?.toIntOrNull()

            val currentPlaylist = playlistManager.getCurrentPlaylist()

            if (currentPlaylist != null && videoIndexToRemove != null) {
                if (videoIndexToRemove >= 0 && videoIndexToRemove < currentPlaylist.videos.size) {
                    val removedVideo = currentPlaylist.videos.removeAt(videoIndexToRemove)
                    playlistManager.savePlaylists()
                    call.respondRedirect("/")
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid video index: $videoIndexToRemove")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid parameters or no current playlist")
            }
        }











        get("/userPlaylistPage") {
            val currentPlaylist = playlistManager.getCurrentPlaylist()
            playlistManager.loadPlaylists()

            if (currentPlaylist != null) {
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
            } else {
                call.respond(HttpStatusCode.BadRequest, "No playlist selected")
            }
        }






        post("/switchPlaylist") {
            val parameters = call.receiveParameters()
            val playlistName = parameters["playlistName"] ?: return@post
            playlistManager.switchPlaylist(playlistName)
            call.respondRedirect("/")
        }


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



        post("/deletePlaylist") {
            val parameters = call.receiveParameters()
            val playlistNameToDelete = parameters["playlistNameToDelete"]

            if (!playlistNameToDelete.isNullOrBlank()) {
                try {
                    // Delete the playlist by name
                    playlistManager.deletePlaylist(playlistNameToDelete)
                    call.respondRedirect("/")
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Error deleting playlist")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid playlist name")
            }
        }
    }
}



