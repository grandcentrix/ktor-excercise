package net.grandcentrix.backend

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*
fun Application.configureRouting(youtubeManager: YouTubeManagerInterface, playlistManager: PlaylistManager) {
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
                                            value = index.toString() // Index als Wert setzen
                                            +if (videoInfo.customName.isNotEmpty()) videoInfo.customName else videoInfo.videoId
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
}
}
