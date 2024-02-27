package net.grandcentrix.backend

import freemarker.cache.ClassTemplateLoader
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.server.util.*
import net.grandcentrix.backend.models.FormActionType
import net.grandcentrix.backend.models.Video
import net.grandcentrix.backend.models.VideoType
import kotlin.test.Test
import kotlin.test.assertEquals

class RoutingTest {

    companion object {
        const val VIDEO_ID = "1YBtzAAChU8"
        const val RANDOM_ID = "EeRfSNx5RhE"
        const val VIDEO_STATUS = "Testing!"

        val video = Video(
            "1YBtzAAChU8",
            "Test Video",
            "https://www.youtube.com/watch?v=1YBtzAAChU8",
            VideoType.CUSTOM,
            "Tests"
        )
        val videos = mutableListOf(video)
        val videoTypes = mutableListOf("Tests")

        private var actionTitle = "Add a new video:"
        private var formAction = "/add-video"
        private var formActionType = FormActionType.ADD.name
        var formAttributes = mutableMapOf("name" to actionTitle, "link" to formAction, "type" to formActionType)
    }

    @Test
    fun testRoot() = testApplication {

        application {
            install(FreeMarker) {
                templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
            }
        }

        routing {
            route("/") {
                get {
                    call.respond(
                        FreeMarkerContent(
                            template = "index.ftl",
                            model = mapOf(
                                "videos" to videos,
                                "randomId" to RANDOM_ID,
                                "status" to VIDEO_STATUS,
                                "formAction" to formAttributes,
                                "video" to video,
                                "videoTypes" to videoTypes
                            )
                        )
                    )
                }

                post("/add-video") {
                    call.respondRedirect("/")
                }

                delete("/{id}/delete") {
                    call.parameters.getOrFail<String>("id")
                    call.respondRedirect("/")
                }

                get("/shuffle") {
                    call.respondRedirect("/")
                }

                get("/style.css") {
                    call.respond(FreeMarkerContent("style.css", null, contentType = ContentType.Text.CSS))
                }
            }
        }

        val testGetVideos = client.get("/")
        assertEquals(HttpStatusCode.OK,testGetVideos.status)

        val testAddVideo = client.post("/add-video") {
            setBody(listOf("link" to "https://www.youtube.com/watch?v=0MiR7bC9B5o&list=PL6NdkXsPL07KN01gH2vucrHCEyyNmVEx4&index=4&pp=iAQB8AUB", "title" to "Test").formUrlEncode())
        }
        assertEquals(HttpStatusCode.Found,testAddVideo.status)

        val testDeleteVideo = client.delete("/${VIDEO_ID}/delete")
        assertEquals(HttpStatusCode.Found,testDeleteVideo.status)
//
//        val testShuffle = client.get("/shuffle")
//        assertEquals(HttpStatusCode.Found, testShuffle.status)

        val testGetStyle = client.get("/style.css")
        assertEquals(HttpStatusCode.OK,testGetStyle.status)

    }
}