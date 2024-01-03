package net.grandcentrix.backend


import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.html.*
import io.ktor.http.HttpStatusCode
import java.io.File
import java.util.regex.Pattern
import java.net.URL
import java.net.MalformedURLException


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureRouting()
    loadYouTubeLinks()
}

val youtubeLinks = mutableListOf<String>()

fun getRandomYouTubeVideoUrl(): String {
    val randomIndex = (0 until youtubeLinks.size).random()
    return youtubeLinks[randomIndex]
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondHtml {
                head {
                    title { +"Ktor Test" }
                }
                body {
                    h1 { +"Random MV player" }
                    iframe {
                        width = "560"
                        height = "315"
                        src = getRandomYouTubeVideoUrl()
                        attributes["allowfullscreen"] = ""
                    }
                    button {
                        onClick = "window.location.reload();"
                        +"Click me for a different random MV!"
                    }

                    h2 { +"Link to each MV" }
                    ul {
                        youtubeLinks.forEach { link ->
                            li {
                                a(href = link) { +link }
                                form(action = "/deleteVideo", method = FormMethod.post) {
                                    hiddenInput {
                                        name = "videoToDelete"
                                        value = link
                                    }
                                    submitInput {
                                        value = "Delete"
                                    }
                                }
                            }
                        }
                    }

                    form(action = "/addVideo", method = FormMethod.post) {
                        textInput {
                            name = "newVideoUrl"
                            placeholder = "Enter YouTube URL"
                        }
                        submitInput {
                            value = "Add new video"
                            onClick = "addNewVideo(); return false;"
                        }
                    }
                }
            }
        }

        post("/addVideo") {
            val parameters = call.receiveParameters()
            val newVideoUrl = parameters["newVideoUrl"]

            if (!newVideoUrl.isNullOrBlank()) {

                val url = URL(newVideoUrl)
                val host = url.host

                if (host == "www.youtube.com" || host == "youtube.com") {
                    val videoId = url.query?.split("v=")?.get(1)?.split("&")?.get(0)

                    if (!videoId.isNullOrBlank()) {
                        val embeddedUrl = "https://www.youtube.com/embed/$videoId"
                        youtubeLinks.add(embeddedUrl)
                        saveYouTubeLinks()
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


        post("/deleteVideo") {
            val parameters = call.receiveParameters()
            val videoToDelete = parameters["videoToDelete"]
            if (!videoToDelete.isNullOrBlank()) {
                youtubeLinks.remove(videoToDelete)
                saveYouTubeLinks()
                call.respondRedirect("/")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid video URL")
            }
        }

    }
}

private fun loadYouTubeLinks() {
    val file = File("youtubeLinks.txt")
    if (file.exists()) {
        youtubeLinks.clear()
        youtubeLinks.addAll(file.readLines())
    }
}

private fun saveYouTubeLinks() {
    val file = File("youtubeLinks.txt")
    file.writeText(youtubeLinks.joinToString("\n"))
}