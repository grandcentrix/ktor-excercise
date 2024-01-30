package net.grandcentrix.backend

import freemarker.cache.ClassTemplateLoader
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.server.util.*
import junit.framework.TestCase.assertEquals
import net.grandcentrix.backend.models.Video
import net.grandcentrix.backend.models.Video.VideoManager.Companion.VideoManagerInstance
import net.grandcentrix.backend.models.Video.VideoManager.Companion.videoID
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull


//import net.grandcentrix.backend.plugins.configureRouting

class ApplicationTest() {
    @Test
    fun testRoot() = testApplication {
        val videos = mutableListOf(Video("1YBtzAAChU8", "Lofi Girl - Christmas"))
        val randomId = "1YBtzAAChU8"
        val videoStatus = "Testing!"

        environment {
            config = MapApplicationConfig("ktor.environment" to "test")
        }
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
                            model = mapOf("videos" to videos, "randomId" to randomId, "status" to videoStatus)
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
//                    call.respondRedirect("/")
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

        val testDeleteVideo = client.delete("/$videoID/delete")
        assertEquals(HttpStatusCode.Found,testDeleteVideo.status)
//
//        val testShuffle = client.get("/shuffle")
//        assertEquals(HttpStatusCode.Found, testShuffle.status)

        val testGetStyle = client.get("/style.css")
        assertEquals(HttpStatusCode.OK,testGetStyle.status)

    }

    @Test
    fun testGetVideos() {
        assertIs<MutableList<Video>>(VideoManagerInstance.getVideos())
        assertNotNull(VideoManagerInstance.getVideos())
    }

    @Test
    fun testAddVideo() {
        val id = "0MiR7bC9B5o"
        val title = "test"
        VideoManagerInstance.addVideo(id, title)
        val video = VideoManagerInstance.getVideos().find { it.id == id }
        assertNotNull(video)
    }

    @Test
    fun testDeleteVideo() {
        val id = "0MiR7bC9B5o"
        VideoManagerInstance.deleteVideo(id)
        val video = VideoManagerInstance.getVideos().find { it.id == id }
        assertNull(video)
    }

}
