package net.grandcentrix.backend


import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.html.*
import kotlinx.html.*
import java.net.URL



fun Application.configureRouting(youtubeManager: YouTubeManagerInterface, playlistManager: PlaylistManager) {
    val userPlaylist = playlistManager.getAllPlaylists()


    routing {

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


                            form(action = "/createPlaylist", method = FormMethod.post) {
                                textInput {
                                    name = "playlistName"
                                    placeholder = "Enter playlist name"
                                }
                                submitInput {
                                    value = "Create Playlist"
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

                    form(action = "/addVideo", method = FormMethod.post) {
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
                            name = "playlistName"
                            // Populate the dropdown with existing playlists
                            playlistManager.getAllPlaylists().forEach { playlist ->
                                option {
                                    value = playlist.name
                                    +playlist.name
                                }
                            }
                        }
                        textInput {
                            name = "videoNumberToAdd"
                            placeholder = "Enter video number to add"
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
            val videoNumberToAdd = parameters["videoNumberToAdd"]?.toIntOrNull()
            val playlistName = parameters["playlistName"]?.trim() // Trim playlist name to remove whitespace

            if (videoNumberToAdd != null && videoNumberToAdd > 0 && playlistName != null && playlistName.isNotBlank()) {
                try {
                    // Get the video info based on the provided number
                    val videoInfo = youtubeManager.getYoutubeLinks().getOrNull(videoNumberToAdd - 1)
                    if (videoInfo != null) {
                        // Add the video to the selected playlist
                        youtubeManager.addVideo(videoInfo.videoId, videoInfo.customName, playlistName)
                        call.respondRedirect("/")
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid video number")
                    }
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
            val videoId = parameters["videoId"]

            if (!videoId.isNullOrBlank()) {
                call.respondHtml {
                    head {
                        meta {
                            httpEquiv = "refresh"
                            content = "0; url=https://www.youtube.com/embed/$videoId"
                        }
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid video ID")
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
            val playlistName = call.parameters["playlistName"]
            val playlist = playlistManager.getAllPlaylists().find { it.name == playlistName }

            if (playlist != null) {
                val playlistHtml = buildString {
                    playlist.videos.forEach { videoInfo ->
                        append("<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/${videoInfo.videoId}\" frameborder=\"0\" allowfullscreen></iframe>")
                    }
                }
                call.respondHtml {
                    body {
                        unsafe {
                            raw(playlistHtml)
                        }
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Playlist not found")
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
                    playlistManager.deletePlaylist(playlistNameToDelete, playlistIndexToDelete)
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
