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
import junit.framework.TestCase
import net.grandcentrix.backend.models.VideoType
import kotlin.test.Test

class ApplicationTest() {

    @Test
    fun testRoot() = testApplication {

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
                            model = mapOf(
                                "videos" to VideoManagerTest.videos,
                                "randomId" to VideoManagerTest.videoId,
                                "status" to VideoManagerTest.videoStatus,
                                "actionTitle" to VideoManagerTest.actionTitle,
                                "buttonAction" to VideoManagerTest.buttonAction,
                                "link" to VideoManagerTest.videoLink,
                                "videoType" to VideoType.entries.dropLast(1)
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
        TestCase.assertEquals(HttpStatusCode.OK,testGetVideos.status)

        val testAddVideo = client.post("/add-video") {
            setBody(listOf("link" to "https://www.youtube.com/watch?v=0MiR7bC9B5o&list=PL6NdkXsPL07KN01gH2vucrHCEyyNmVEx4&index=4&pp=iAQB8AUB", "title" to "Test").formUrlEncode())
        }
        TestCase.assertEquals(HttpStatusCode.Found,testAddVideo.status)

        val testDeleteVideo = client.delete("/${VideoManagerTest.videoId}/delete")
        TestCase.assertEquals(HttpStatusCode.Found,testDeleteVideo.status)
//
//        val testShuffle = client.get("/shuffle")
//        assertEquals(HttpStatusCode.Found, testShuffle.status)

        val testGetStyle = client.get("/style.css")
        TestCase.assertEquals(HttpStatusCode.OK,testGetStyle.status)

    }
}