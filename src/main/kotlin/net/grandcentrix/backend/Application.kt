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
            val youtubeUrlRegex = "^https?://(?:www\\.)?youtube\\.com/watch\\?(?=.*v=\\w+)(?:\\S+)?$".toRegex()

            if (!newVideoUrl.isNullOrBlank() && newVideoUrl.matches(youtubeUrlRegex)) {
                // Extract video ID from the YouTube URL
                val videoId = extractYouTubeVideoId(newVideoUrl)

                if (videoId != null) {
                    // Construct the embedded YouTube URL
                    val embeddedUrl = "https://www.youtube.com/embed/$videoId"

                    youtubeLinks.add(embeddedUrl)
                    saveYouTubeLinks()
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid YouTube URL")
                }
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

private fun extractYouTubeVideoId(url: String): String? {
    val pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed\\\\%2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*"
    val compiledPattern = Pattern.compile(pattern)
    val matcher = compiledPattern.matcher(url)

    return if (matcher.find()) {
        matcher.group()
    } else {
        null
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
