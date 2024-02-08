package net.grandcentrix.backend.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import net.grandcentrix.backend.models.FormActionType
import net.grandcentrix.backend.models.FormActionType.Companion.getFormAction
import net.grandcentrix.backend.models.FormActionType.Companion.getFormTitle
import net.grandcentrix.backend.models.FormManager
import net.grandcentrix.backend.models.VideoManager
import net.grandcentrix.backend.models.VideoType

fun Application.configureRouting(videoManager: VideoManager, formManager: FormManager) {
    routing {
        route("/") {
            get {
                call.respond(FreeMarkerContent("index.ftl",
                    mapOf(
                        "videos" to videoManager.getVideos(),
                        "randomId" to videoManager.shuffle(),
                        "status" to formManager.getStatus(),
                        "formAction" to formManager.getFormActionType(),
                        "link" to formManager.getLink(),
                        "videoType" to VideoType.entries.dropLast(1)
                    )
                ))
            }

            post("/add-video") {
                val formParameters = call.receiveParameters()
                formManager.setVideoParameters(formParameters)
                call.respondRedirect("/")
            }

            get("/{id}/delete") {
                val id = call.parameters.getOrFail<String>("id")
                videoManager.deleteVideo(id)
                call.respondRedirect("/")
            }

            get("/{id}/update") {
                val id = call.parameters.getOrFail<String>("id")
                val video = videoManager.getVideos().single { it.id == id }
                formManager.updateFormAction(id, video)
                call.respondRedirect("/")
            }

            post("/{id}/update") {
                val id = call.parameters.getOrFail<String>("id")
                val formParameters = call.receiveParameters()
                formManager.setUpdatedVideoParameters(id, formParameters)
                videoManager.updateVideo()
                call.respondRedirect("/")
            }

            get("/{videoType}/videos") {
                val videoType = call.parameters.getOrFail<String>("videoType")
                val videos = videoManager.getVideosByType(videoType)
                call.respond(FreeMarkerContent("videosByType.ftl",
                    mapOf(
                        "videos" to videos,
                        "randomId" to videoManager.shuffleByType(videoType),
                        "status" to formManager.getStatus(),
                        "formAction" to formManager.getFormActionType(),
                        "link" to formManager.getLink(),
                        "videoType" to videoType
                    )
                ))
            }

            get("/cancel") {
                formManager.setActionTitle(getFormTitle(FormActionType.ADD))
                formManager.setFormAction(getFormAction(FormActionType.ADD))
//                call.respondRedirect(path)
            }

            get("/shuffle") {
//                call.respondRedirect(path)
            }

            get("/style.css") {
                call.respond(FreeMarkerContent("style.css", null, contentType = ContentType.Text.CSS))
            }
        }

        route("/{videoType}/{id}") {
            get("/update") {
                val id = call.parameters.getOrFail<String>("id")
                val videoType = call.parameters.getOrFail<String>("videoType")
                val video = videoManager.getVideos().single { it.id == id }
                formManager.updateFormAction(id, video)
//                get uri from parent
                call.respondRedirect("/$videoType/videos")
            }

            post("/{id}/update") {
                val id = call.parameters.getOrFail<String>("id")
                val videoType = call.parameters.getOrFail<String>("videoType")
                val formParameters = call.receiveParameters()
                formManager.setUpdatedVideoParameters(id, formParameters)
                call.respondRedirect("/$videoType/videos")
            }
        }
    }
}