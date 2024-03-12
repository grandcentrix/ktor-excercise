package net.grandcentrix.backend

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*

fun Application.configureRouting(youtubeManager: YouTubeManagerInterface, playlistManager: PlayListInterface) {
    configurePostRoutes(youtubeManager, playlistManager)

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

                            form(action = "/renamePlaylist", method = FormMethod.post) {
                                select {
                                    name = "playlistNameToRename"
                                    playlistManager.getAllPlaylists().forEach { playlist ->
                                        option {
                                            value = playlist.name
                                            +playlist.name
                                        }
                                    }
                                }
                                textInput {
                                    name = "newPlaylistName"
                                    placeholder = "Enter new playlist name"
                                }
                                submitInput {
                                    value = "Rename Playlist"
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

                            form(action = "/removeVideo", method = FormMethod.post) {
                                val currentPlaylist = playlistManager.getCurrentPlaylist()
                                if (currentPlaylist != null) {
                                    select {
                                        name = "videoIndexToRemove"
                                        currentPlaylist.videos.forEachIndexed { index, videoInfo ->
                                            option {
                                                value = index.toString() // Index as value
                                                +if (videoInfo.customName.isNotEmpty()) "${videoInfo.customName} (${videoInfo.videoId})" else videoInfo.videoId
                                            }
                                        }
                                    }
                                    submitInput {
                                        value = "Remove Selected Video"
                                    }
                                }
                            }
                        }
                    }

                    h3 { +"Link to each MV" }
                    val videosPerPage = 5
                    val totalVideos = youtubeManager.getYoutubeLinks().size
                    val totalPages = (totalVideos + videosPerPage - 1) / videosPerPage // Calculate total pages
                    val currentPage = call.parameters["page"]?.toIntOrNull() ?: 1 // Get current page or default to 1

                    val startIndex = (currentPage - 1) * videosPerPage
                    val endIndex = minOf(currentPage * videosPerPage, totalVideos)

                    ul {
                        youtubeManager.getYoutubeLinks().subList(startIndex, endIndex).forEachIndexed { index, videoInfo ->
                            li {
                                val videoNumber = index + 1 + startIndex
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

                    if (totalPages > 1) {
                        p {
                            if (currentPage > 1) {
                                a(href = "?page=${currentPage - 1}") { +"Previous" }
                            }
                            +" Page $currentPage of $totalPages "
                            if (currentPage < totalPages) {
                                a(href = "?page=${currentPage + 1}") { +"Next" }
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
                                    val nameToDisplay = if (videoInfo.customName.isNotEmpty()) videoInfo.customName else videoInfo.videoId ?: "No Name Available"
                                    value = videoInfo.videoId // Set the value to the video ID
                                    +nameToDisplay
                                }
                            }
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

                    form(action = "/addTagToVideo", method = FormMethod.post) {
                        select {
                            name = "videoId" // Set the name to "videoId"
                            // Populate the dropdown menu with video IDs and custom names
                            youtubeManager.getYoutubeLinks().forEachIndexed { index, videoInfo ->
                                option {
                                    val nameToDisplay = if (videoInfo.customName.isNotEmpty()) videoInfo.customName else "No Name Available"
                                    value = videoInfo.videoId // Set the value to the video ID
                                    +nameToDisplay
                                }
                            }
                        }
                        textInput {
                            name = "tagName"
                            placeholder = "Enter tag"
                        }
                        submitInput {
                            value = "Add Tag to Video"
                        }
                    }

                    form(action = "/filterVideosByTag", method = FormMethod.post) {
                        select {
                            name = "tagToFilter" // Set the name to "tagToFilter"
                            // Populate the dropdown menu with existing tags
                            val existingTags = youtubeManager.getAllTags()
                            existingTags.forEach { tag ->
                                option {
                                    value = tag // Set the value to the tag name
                                    +tag // Display the tag name in the dropdown menu
                                }
                            }
                        }
                        submitInput {
                            value = "Filter Videos by Tag"
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

                    form(action = "/deleteTagFromVideo", method = FormMethod.post) {
                        select {
                            name = "videoId" // Set the name to "videoId"
                            // Populate the dropdown menu with video IDs and custom names
                            youtubeManager.getYoutubeLinks().forEachIndexed { index, videoInfo ->
                                option {
                                    val nameToDisplay = if (videoInfo.customName.isNotEmpty()) videoInfo.customName else "No Name Available"
                                    value = videoInfo.videoId // Set the value to the video ID
                                    +nameToDisplay
                                }
                            }
                        }
                        select {
                            name = "tagToDelete" // Set the name to "tagToDelete"
                            val existingTags = youtubeManager.getAllTags()
                            existingTags.forEach { tag ->
                                option {
                                    value = tag // Set the value to the tag name
                                    +tag // Display the tag name in the dropdown menu
                            }
                        }
                        submitInput {
                            value = "Delete Tag"
                        }
                    }




                }
            }
        }
    }
}
}
