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



fun Application.configureRouting(youtubeManager: YouTubeManagerInterface, playlistManager: PlaylistManager) {


    routing {
        val userPlaylist = playlistManager.getAllPlaylists()

        get("/") {
            call.respondHtml {
                head {
                    title { +"Ktor Test" }
                    style {
                        unsafe {
                            +" .grid-container { display: grid; grid-template-columns: 2fr 1fr; }"
                            +"body { background-color: black; color: darkgray; }"
                            +"button { background-color: gray; color: white; }"
                            +"input { background-color: darkgray; color: white; }"
                            +"select { background-color: darkgray; color: white; }"
                            +"a { color: darkgrey; }"
                        }
                    }
                }
                body {
                    div(classes = "grid-container") {
                        div(classes = "random-video") {
                            h1 { +"Random MV player" }
                            iframe {
                                width = "560"
                                height = "315"
                                src = youtubeManager.getRandomYouTubeVideoUrl()
                                attributes["allowfullscreen"] = ""
                            }
                            button {
                                onClick = "window.location.reload();"
                                +"Click me for a different random MV!"
                            }
                        }

                        div(classes = "playlist") {
                            h3 { +"Your Playlist" }

                            iframe {
                                id = "playlistVideoPlayer"
                                width = "560"
                                height = "315"
                                src = "/userPlaylistPage"
                                attributes["allowfullscreen"] = ""
                            }

                            form(action = "/switchPlaylist", method = FormMethod.post) {
                                select {
                                    name = "playlistName"
                                    playlistManager.getAllPlaylists().forEach { playlist ->
                                        option {
                                            value = playlist.name
                                            +playlist.name
                                        }
                                    }
                                }
                                submitInput {
                                    value = "Switch Playlist"
                                }
                            }



                            form(action = "/savePlaylists", method = FormMethod.post) {
                                submitInput {
                                    value = "Save Playlists"
                                }
                            }



                            form(action = "/deletePlaylist", method = FormMethod.post) {
                                select {
                                    name = "playlistNameToDelete"
                                    playlistManager.getAllPlaylists().forEach { playlist ->
                                        option {
                                            value = playlist.name
                                            +playlist.name
                                        }
                                    }
                                }
                                submitInput {
                                    value = "Delete Playlist"
                                }
                            }


                            // "Remove Selected Video" form placed under "Your Playlist"
                            form(action = "/removeVideo", method = FormMethod.post) {
                                select {
                                    id = "playlistSelectToRemove"
                                    name = "videoIdToRemove"
                                    userPlaylist.forEach { playlist ->
                                        playlist.videos.forEach { videoInfo ->
                                            option {
                                                value = videoInfo.videoId
                                                +if (videoInfo.customName.isNotEmpty()) videoInfo.customName else videoInfo.videoId
                                            }
                                        }
                                    }
                                }
                                submitInput {
                                    value = "Remove Selected Video"
                                }
                            }
                        }
                    }

                    h3 { +"Link to each MV" }
                    ul {
                        youtubeManager.getYoutubeLinks().forEachIndexed { index, videoInfo ->
                            li {
                                val videoNumber = index + 1
                                val videoUrl = "https://www.youtube.com/watch?v=${videoInfo.videoId}"
                                +"$videoNumber. "
                                a(href = videoUrl, target = "_blank") { +if (videoInfo.customName.isNotEmpty()) videoInfo.customName else videoUrl }
                                form(action = "/deleteVideo", method = FormMethod.post) {
                                    hiddenInput {
                                        name = "videoToDelete"
                                        value = videoInfo.videoId
                                    }
                                }
                            }
                        }
                    }

                    form(action = "/deleteVideoByNumber", method = FormMethod.post) {
                        textInput {
                            name = "videoNumberToDelete"
                            placeholder = "Enter video number"
                        }
                        submitInput {
                            value = "Delete"
                        }
                    }

                    form(action = "/addVideos", method = FormMethod.post) {
                        textInput {
                            name = "newVideoUrl"
                            placeholder = "Enter YouTube URL"
                        }
                        textInput {
                            name = "customName"
                            placeholder = "Enter custom name"
                        }
                        submitInput {
                            value = "Add new video"
                            onClick = "addNewVideo(); return false;"
                        }
                    }



                    form(action = "/addVideoToPlaylist", method = FormMethod.post) {
                        select {
                            name = "videoId" // Set the name attribute to "videoId"
                            // Populate the dropdown with video numbers and corresponding custom names or video IDs
                            youtubeManager.getYoutubeLinks().forEachIndexed { index, videoInfo ->
                                option {
                                    val videoId = videoInfo.videoId // Extracted video ID
                                    value = videoId // Set the value to the video ID
                                    +if (videoInfo.customName.isNotEmpty()) "${videoInfo.customName} ($videoId)" else videoId
                                }
                            }
                        }
                        textInput {
                            name = "customName"
                            placeholder = "Enter custom name (optional)"
                        }
                        select {
                            name = "playlistName"
                            // Populate the dropdown with existing playlists
                            playlistManager.getAllPlaylists().forEach { playlist ->
                                option {
                                    value = playlist.name
                                    +playlist.name
                                }
                            }
                        }
                        submitInput {
                            value = "Add Video to Playlist"
                        }
                    }


                    form(action = "/renameVideoByNumber", method = FormMethod.post) {
                        textInput {
                            name = "videoNumberToRename"
                            placeholder = "Enter video number to rename"
                        }
                        textInput {
                            name = "newCustomName"
                            placeholder = "Enter new custom name"
                        }
                        submitInput {
                            value = "Rename Video"
                        }
                    }
                    form(action = "/createPlaylist", method = FormMethod.post) {
                        textInput {
                            name = "playlistName"
                            placeholder = "Enter playlist name"
                        }
                        submitInput {
                            value = "Create Playlist"
                        }
                    }

                }
            }
        }





        post("/addVideoToPlaylist") {
            val parameters = call.receiveParameters()
            val videoId = parameters["videoId"]
            val customName = parameters["customName"]
            val playlistName = parameters["playlistName"]

            println("Received parameters: videoId=$videoId, customName=$customName, playlistName=$playlistName")

            if (videoId != null && playlistName != null && playlistName.isNotBlank()) {
                try {
                    // Add a delay to simulate loading time
                    delay(1000)

                    // Add the video to the selected playlist
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
                            youtubeManager.addVideo(videoId, customName, playlistName) // Pass playlistName parameter
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

        post("/savePlaylists") {
            playlistManager.savePlaylists()
            call.respondRedirect("/")
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
        post("/playPlaylistVideo") {
            val parameters = call.receiveParameters()
            val playlistName = parameters["playlistName"]
            val playlist = playlistManager.getAllPlaylists().find { it.name == playlistName }

            val firstVideoId = playlist?.videos?.firstOrNull()?.videoId

            if (!firstVideoId.isNullOrBlank()) {
                call.respondHtml {
                    head {
                        meta {
                            httpEquiv = "refresh"
                            content = "0; url=https://www.youtube.com/embed/$firstVideoId"
                        }
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Playlist not found or no videos in playlist")
            }
        }

        post("/removeVideo") {
            val parameters = call.receiveParameters()
            val videoIdToRemove = parameters["videoIdToRemove"]

            if (!videoIdToRemove.isNullOrBlank()) {
                if (youtubeManager.removeVideo(videoIdToRemove)) {
                    call.respondRedirect("/")
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Video not found in the playlist")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid video ID")
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
            val playlistIndexToDelete = parameters["playlistIndexToDelete"]?.toIntOrNull()

            if (!playlistNameToDelete.isNullOrBlank() && playlistIndexToDelete != null) {
                try {
                    // Delete the playlist immediately from memory
                    playlistManager.deletePlaylist(playlistNameToDelete, playlistIndexToDelete)
                    // No need to sleep here
                    call.respondRedirect("/")
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Error deleting playlist")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid playlist name or index")
            }
        }
    }
}