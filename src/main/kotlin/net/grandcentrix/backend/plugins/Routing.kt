package net.grandcentrix.backend.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
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
                        "status" to formManager.status,
                        "formAction" to formManager.formAttributes,
                        "video" to formManager.video,
                        "videoTypes" to formManager.videoTypes
                    )
                ))
            }

            post("/add-video") {
                val formParameters = call.receiveParameters()
                formManager.setVideoParameters(formParameters)
                videoManager.addVideo()
                call.respondRedirect("/")
            }

            get("/shuffle") {
                call.respondRedirect("/")
            }

            get("{videoType}/shuffle") {
                val videoType = call.parameters.getOrFail<String>("videoType")
                call.respondRedirect("/$videoType/videos")
            }

            get("/style.css") {
                call.respond(FreeMarkerContent("style.css", null, contentType = ContentType.Text.CSS))
            }

            get("/{videoType}/videos") {
                val videoType = call.parameters.getOrFail<VideoType>("videoType")
                call.respond(FreeMarkerContent("videosByType.ftl",
                    mapOf(
                        "videos" to videoManager.getVideosByType(videoType),
                        "randomId" to videoManager.shuffleByType(videoType),
                        "status" to formManager.status,
                        "formAction" to formManager.formAttributes,
                        "video" to formManager.video,
                        "videoType" to videoType,
                        "videoTypes" to VideoType.entries.dropLast(1)
                    )
                ))
            }
        }

        route("/{id}") {
            get("/delete") {
                val id = call.parameters.getOrFail<String>("id")
                videoManager.deleteVideo(id)
                call.respondRedirect("/")
            }

            get("/update") {
                val id = call.parameters.getOrFail<String>("id")
                val video = videoManager.getVideos().single { it.id == id }
                formManager.updateFormAction(id, video)
                call.respondRedirect("/")
            }

            post("/update") {
                val id = call.parameters.getOrFail<String>("id")
                val formParameters = call.receiveParameters()
                formManager.setUpdatedVideoParameters(id, formParameters)
                videoManager.updateVideo()
                call.respondRedirect("/")
            }

            get("/update/cancel") {
                formManager.revertForm()
                call.respondRedirect("/")
            }
        }

        route("/{videoType}") {

            post("/add-video") {
                val videoType = call.parameters.getOrFail<String>("videoType")
                val formParameters = call.receiveParameters()
                formManager.setVideoParameters(formParameters)
                videoManager.addVideo()
                call.respondRedirect("/$videoType/videos")
            }

            get("/{id}/delete") {
                val id = call.parameters.getOrFail<String>("id")
                val videoType = call.parameters.getOrFail<String>("videoType")
                videoManager.deleteVideo(id)
                call.respondRedirect("/$videoType/videos")
            }

            get("/{id}/update") {
                val id = call.parameters.getOrFail<String>("id")
                val videoType = call.parameters.getOrFail<String>("videoType")
                val video = videoManager.getVideos().single { it.id == id }
                formManager.updateFormAction(id, video)
                call.respondRedirect("/$videoType/videos")
            }

            post("/{id}/update") {
                val id = call.parameters.getOrFail<String>("id")
                val videoType = call.parameters.getOrFail<String>("videoType")
                val formParameters = call.receiveParameters()
                formManager.setUpdatedVideoParameters(id, formParameters)
                videoManager.updateVideo()
                call.respondRedirect("/$videoType/videos")
            }

            get("/{id}/update/cancel") {
                val videoType = call.parameters.getOrFail<String>("videoType")
                formManager.revertForm()
                call.respondRedirect("/$videoType/videos")
            }
        }
    }
}