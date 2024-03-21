package net.grandcentrix.backend

import freemarker.cache.ClassTemplateLoader
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.freemarker.*
import io.ktor.server.testing.*
import net.grandcentrix.backend.models.FormManager.Companion.FormManagerInstance
import net.grandcentrix.backend.models.VideoManager.Companion.VideoManagerInstance
import net.grandcentrix.backend.models.VideoType
import net.grandcentrix.backend.plugins.configureRouting
import net.grandcentrix.backend.plugins.configureStatusPages
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class RoutingStatusPagesTest {

    private val videoType = VideoType.MUSIC
    private val videoID = "qU9mHegkTc4" // must exist in the videosList.json file

    @Test
    fun testNotFoundPage() = testApplication {
        application {
            configureStatusPages()
        }

        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }

        val response = client.get("/videos")

        assertContains(response.bodyAsText(), "Oops! It wasn't possible to find the page, or it doesn't exist.")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testNotAuthorized() = testApplication {
        application {
            configureStatusPages()
        }

        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }

        //TODO

//        assertContains(response.bodyAsText(), "Error 401. Not authorized.")
//        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testAccessNotAllowed() = testApplication {
        application {
            configureStatusPages()
        }

        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }

        //TODO

//        assertContains(response.bodyAsText(), "Error 403. Access not allowed.")
//        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testAddVideoMissingRequestParameter() = testApplication {
        application {
            configureRouting(VideoManagerInstance, FormManagerInstance)
            configureStatusPages()
        }

        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }

        val response = client.post("/add-video") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf(
                "link" to "",
                "title" to "",
                "videoTypes" to videoType.name,
                "customType" to ""
            ).formUrlEncode())
        }

        assertEquals(HttpStatusCode.Found, response.status)
//        assertContains(response.bodyAsText(), "Video link and title cannot be blank!")
    }

    @Test
    fun testVideosTypeAddVideoMissingRequestParameter() = testApplication {
        application {
            configureRouting(VideoManagerInstance, FormManagerInstance)
            configureStatusPages()
        }

        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }

        val response = client.post("/${videoType.name}/add-video") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf(
                "link" to "",
                "title" to "",
                "videoTypes" to videoType.name,
                "customType" to ""
            ).formUrlEncode())
        }

//        assertContains(response.bodyAsText(), "Video link and title cannot be blank!")
        assertEquals(HttpStatusCode.Found, response.status)
    }

    @Test
    fun testUpdateVideoMissingRequestParameter() = testApplication {
        application {
            configureRouting(VideoManagerInstance, FormManagerInstance)
            configureStatusPages()
        }

        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }

        val response = client.post("/$videoID/update") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf(
                "title" to "",
                "videoTypes" to VideoType.CUSTOM.name,
                "customType" to ""
            ).formUrlEncode())
        }

//        assertContains(response.bodyAsText(), "Custom type name cannot be blank!")
        assertEquals(HttpStatusCode.Found, response.status)
    }

    @Test
    fun testVideosTypeUpdateVideoMissingRequestParameter() = testApplication {
        application {
            configureRouting(VideoManagerInstance, FormManagerInstance)
            configureStatusPages()
        }

        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }

        val response = client.post("/${videoType.name}/$videoID/update") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf(
                "title" to "",
                "videoTypes" to VideoType.CUSTOM.name,
                "customType" to ""
            ).formUrlEncode())
        }

//        assertContains(response.bodyAsText(), "Custom type name cannot be blank!")
        assertEquals(HttpStatusCode.Found, response.status)
    }

    @Test
    fun testNoSuchElementException() = testApplication {
        application {
            configureStatusPages()
        }

        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }
//
        //TODO

//        assertContains(response.bodyAsText(), "Error 401. Not authorized.")
//        assertEquals(HttpStatusCode.OK, response.status)
    }

}