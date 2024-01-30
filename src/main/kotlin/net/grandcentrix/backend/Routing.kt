package net.grandcentrix.backend

import YouTubeManagerInterface
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.html.*
import kotlinx.html.*
import java.net.URL


fun Application.configureRouting(youtubeManager: YouTubeManagerInterface) {
    routing {
        get("/") {
            val randomVideoUrl = youtubeManager.getRandomYouTubeVideoUrl()
            val playlist = youtubeManager.getUserPlaylist()

            call.respondHtml {
                head {
                    title { +"Ktor Test" }
                }
                body {
                    h1 { +"Random MV player" }
                    iframe {
                        width = "560"
                        height = "315"
                        src = randomVideoUrl
                        attributes["allowfullscreen"] = ""
                    }
                    button {
                        onClick = "window.location.reload();"
                        +"Click me for a different random MV!"
                    }

                    h2 { +"Playlist" }
                    playlist.forEach { videoInfo ->
                        iframe {
                            width = "560"
                            height = "315"
                            src = "https://www.youtube.com/embed/${videoInfo.videoId}"
                            attributes["allowfullscreen"] = ""
                        }
                        br {}
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

                    form(action = "/addVideoByNumber", method = FormMethod.post) {
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
                }
            }
        }

        post("/addVideoByNumber") {
            val parameters = call.receiveParameters()
            val videoNumberToAdd = parameters["videoNumberToAdd"]?.toIntOrNull()

            if (videoNumberToAdd != null && videoNumberToAdd > 0 && videoNumberToAdd <= youtubeManager.getYoutubeLinks().size) {
                val indexToAdd = videoNumberToAdd - 1
                val videoToAdd = youtubeManager.getYoutubeLinks()[indexToAdd]
                youtubeManager.addVideoToPlaylist(videoToAdd.videoId, videoToAdd.customName, true) // Set addToUserPlaylist to true
                call.respondRedirect("/")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid video number")
            }
        }

        post("/addVideo") {
            val parameters = call.receiveParameters()
            val newVideoUrl = parameters["newVideoUrl"]
            val customName = parameters["customName"] ?: ""

            if (!newVideoUrl.isNullOrBlank()) {
                val url = URL(newVideoUrl)
                val host = url.host

                if (host == "www.youtube.com" || host == "youtube.com") {
                    val videoId = url.query?.split("v=")?.get(1)?.split("&")?.get(0)

                    if (!videoId.isNullOrBlank()) {
                        youtubeManager.addVideo(videoId, customName, false) // Set addToUserPlaylist to false
                        youtubeManager.saveYouTubeLinks() // Save the updated youtubeLinks
                        call.respondRedirect("/")
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
    }
}


