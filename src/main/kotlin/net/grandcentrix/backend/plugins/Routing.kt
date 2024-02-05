package net.grandcentrix.backend.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import net.grandcentrix.backend.models.VideoManager
import net.grandcentrix.backend.models.VideoManager.Companion.actionTitle
import net.grandcentrix.backend.models.VideoManager.Companion.buttonAction
import net.grandcentrix.backend.models.VideoManager.Companion.link
import net.grandcentrix.backend.models.VideoType

fun Application.configureRouting(videoManager: VideoManager) {
    routing {
        route("/") {
            get {
                call.respond(FreeMarkerContent("index.ftl",
                    mapOf(
                        "videos" to videoManager.getVideos(),
                        "randomId" to videoManager.shuffle(),
                        "status" to videoManager.status,
                        "actionTitle" to actionTitle,
                        "buttonAction" to buttonAction,
                        "link" to link,
                        "videoType" to VideoType.entries.dropLast(1)
                    )
                ))
            }

            post("/add-video") {
                val formParameters = call.receiveParameters()
                videoManager.getVideoData(formParameters)
                call.respondRedirect("/")
            }

            get("/{id}/delete") {
                val id = call.parameters.getOrFail<String>("id")
                videoManager.deleteVideo(id)
                call.respondRedirect("/")
            }

            get("/{id}/update") {
                val id = call.parameters.getOrFail<String>("id")
                videoManager.updateForm(id)
                call.respondRedirect("/")
            }

            post("/{id}/update") {
                val id = call.parameters.getOrFail<String>("id")
                val formParameters = call.receiveParameters()
                videoManager.getUpdatedData(id, formParameters)
                call.respondRedirect("/")
            }

            get("/{videoType}/videos") {
                val videoType = call.parameters.getOrFail<String>("videoType")
                val videos = videoManager.getVideosByType(videoType)
                call.respond(FreeMarkerContent("videosByType.ftl",
                    mapOf(
                        "videos" to videos,
                        "randomId" to videoManager.shuffle(),
                        "status" to videoManager.status,
                        "actionTitle" to actionTitle,
                        "buttonAction" to buttonAction,
                        "link" to link,
                        "videoType" to videoType
                    )
                ))
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