package net.grandcentrix.backend.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import net.grandcentrix.backend.models.Video
import net.grandcentrix.backend.models.Video.VideoManager.Companion.actionTitle
import net.grandcentrix.backend.models.Video.VideoManager.Companion.buttonAction
import net.grandcentrix.backend.models.Video.VideoManager.Companion.videoLink

fun Application.configureRouting(manager: Video.VideoManagerInterface) {
    routing {
        route("/") {
            get {
                call.respond(FreeMarkerContent("index.ftl", mapOf("videos" to manager.getVideos(), "randomId" to manager.shuffle(), "status" to manager.status, "actionTitle" to actionTitle, "buttonAction" to buttonAction, "link" to videoLink)))
            }

            post("/add-video") {
                val formParameters = call.receiveParameters()
                val id = formParameters.getOrFail("link").substringAfter("v=").substringBefore("&")
                val link = formParameters.getOrFail("link")
                val title = formParameters.getOrFail("title")
                if (id.isBlank() || title.isBlank()) {
                    manager.status = "Video link and title cannot be blank or video link is not supported!"
                    call.respondRedirect("/")
                } else if (!(link.startsWith("https://www.youtube.com/watch?v=") || link.startsWith("https://youtube.com/watch?v=") || link.startsWith("youtube.com/watch?v=") || link.startsWith("www.youtube.com/watch?v="))) {
                    manager.status = "Video link is not supported!"
                    call.respondRedirect("/")
                } else {
                    manager.addVideo(id, title, link)
                    call.respondRedirect("/")
                }
            }

            get("/{id}/delete") {
                val id = call.parameters.getOrFail<String>("id")
                manager.deleteVideo(id)
                call.respondRedirect("/")
            }

            get("/{id}/update") {
                val id = call.parameters.getOrFail<String>("id")
                val videoLink = manager.updateForm(id)
                call.respondRedirect("/")
            }

            post("/{id}/update") {
                val id = call.parameters.getOrFail<String>("id")
                val formParameters = call.receiveParameters()
                val newTitle = formParameters.getOrFail("title")

                if (newTitle.isBlank()) {
                    manager.status = "Video title cannot be blank!"
                    call.respondRedirect("/")
                } else {
                    manager.updateVideo(id, newTitle)
                    actionTitle = "Add a new video:"
                    buttonAction = "/add-video"
                    call.respondRedirect("/")

                }
            }

            get("/shuffle") {
                call.respondRedirect("/")
            }

            get("/style.css") {
                call.respond(FreeMarkerContent("style.css", null, contentType = ContentType.Text.CSS))
            }
        }
    }
}