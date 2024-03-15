package net.grandcentrix.backend

import freemarker.cache.ClassTemplateLoader
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.freemarker.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import net.grandcentrix.backend.models.FormActionType
import net.grandcentrix.backend.models.FormManager.Companion.FormManagerInstance
import net.grandcentrix.backend.models.StorageManagerMemory.Companion.StorageManagerMemoryInstance
import net.grandcentrix.backend.models.StorageManagerTypesFile.Companion.StorageManagerTypesFileInstance
import net.grandcentrix.backend.models.Video
import net.grandcentrix.backend.models.VideoManager.Companion.VideoManagerInstance
import net.grandcentrix.backend.models.VideoType
import net.grandcentrix.backend.plugins.configureRouting
import org.junit.Before
import java.io.File
import kotlin.test.*

class RoutingTest {

    companion object {
        private const val FILE_NAME = "src/main/resources/testFile.json"
        // populates testFile with type names from videoTypes.json
        val typeNames = StorageManagerTypesFileInstance.getContent()
        val VIDEO_TYPE = VideoType.MUSIC

        val video1 = Video(
            "1YBtzAAChU8",
            "Test Video",
            "https://www.youtube.com/watch?v=1YBtzAAChU8",
            VIDEO_TYPE,
            ""
        )

        val video2 = Video(
            "1bvbsx-hpFc",
            "Lo-fi Summer",
            "https://www.youtube.com/watch?v=1bvbsx-hpFc",
            VideoType.NEWS,
            ""
        )

        val video3 = Video(
            "IXwVSUexFyM",
            "Lorde - The Path",
            "https://www.youtube.com/watch?v=IXwVSUexFyM",
            VIDEO_TYPE,
            ""
        )

        val videos = mutableListOf(video1, video2, video3)

        private var actionTitleAdd = "Add a new video:"
        private var formAction = "/add-video"
        private var formActionType = FormActionType.ADD.name
    }

    @Before
    fun beforeTests() = testApplication {

        mockkStatic(::saveVideos)
        every { saveVideos() } returns false

        val typeNamesJson = Json.encodeToJsonElement(typeNames).toString()
        File(FILE_NAME).writeText(typeNamesJson)
        mockkObject(StorageManagerTypesFileInstance, recordPrivateCalls = true)
        every { StorageManagerTypesFileInstance["getFile"]() } returns File(FILE_NAME)

        mockkObject(StorageManagerMemoryInstance, recordPrivateCalls = true)
        every { StorageManagerMemoryInstance.videos } returns videos

        mockkObject(VideoManagerInstance)
        VideoManagerInstance.loadVideosToTypeList(videos)

        mockkObject(FormManagerInstance)
    }

    @AfterTest
    fun afterTest() {
        unmockkAll()
        File(FILE_NAME).writeText("[]")
    }

    @Test
    fun testPostVideo() = testApplication {

        application {
            configureRouting(VideoManagerInstance, FormManagerInstance)
        }

        val response = client.post("/add-video") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf(
                "link" to "https://www.youtube.com/watch?v=wBfVsucRe1w",
                "title" to "Chico Buarque - Construção",
                "videoTypes" to VIDEO_TYPE.name,
                "customType" to ""
            ).formUrlEncode())
        }

        assertEquals(HttpStatusCode.Found, response.status)
        assertNotNull(StorageManagerMemoryInstance.videos.find { it.id == "wBfVsucRe1w" })
    }

    @Test
    fun testGetVideos() = testApplication {

        application {
            configureRouting(VideoManagerInstance, FormManagerInstance)
        }

        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }

        val response: HttpStatusCode = client.get("/").body()

        assertEquals(HttpStatusCode.OK,response)
    }

    @Test
    fun testDeleteVideo() = testApplication {

        application {
            configureRouting(VideoManagerInstance, FormManagerInstance)
        }

        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }

        val response = client.get("/${video2.id}/delete")

        assertEquals(HttpStatusCode.OK,response.status)
        assertNull(StorageManagerMemoryInstance.videos.find { it.id == video2.id })
        assertContains(response.bodyAsText(), "Video deleted!")
    }

    @Test
    fun testGetUpdateVideo() = testApplication{

        application {
            configureRouting(VideoManagerInstance, FormManagerInstance)
        }

        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }

        val response = client.get("/${video1.id}/update")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testPostUpdateVideo() = testApplication {

        application {
            configureRouting(VideoManagerInstance, FormManagerInstance)
        }

        val response = client.post("/${video1.id}/update") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf(
                "title" to "Christmas",
                "videoTypes" to "LO-FI", // necessary to be an existing type in videoTypes.json
                "customType" to ""
            ).formUrlEncode())
        }

        assertEquals(HttpStatusCode.Found, response.status)

        assertEquals(
            "Christmas",
            StorageManagerMemoryInstance.videos.find { it.id == video1.id }!!.title
        )

        assertEquals(
            "LO-FI",
            StorageManagerMemoryInstance.videos.find { it.id == video1.id }!!.customTypeName
        )

        assertEquals(
            VideoType.CUSTOM,
            StorageManagerMemoryInstance.videos.find { it.id == video1.id }!!.videoType
        )
    }

    @Test
    fun testShuffle() = testApplication {
        application {
            configureRouting(VideoManagerInstance, FormManagerInstance)
        }

        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }

        val response = client.get("/shuffle")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testCancelUpdateVideo() = testApplication {
        application {
            configureRouting(VideoManagerInstance, FormManagerInstance)
        }

        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }

        val response = client.get("/${video1.id}/update/cancel")

        assertContains(response.bodyAsText(), actionTitleAdd)
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testGetVideosByType() = testApplication {
        application {
            configureRouting(VideoManagerInstance, FormManagerInstance)
        }

        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }

        val response = client.get("/${VIDEO_TYPE}/videos") // necessary to be a type of some video in the list
        assertEquals(HttpStatusCode.OK,response.status)
    }

    @Test
    fun testShuffleVideoByType() = testApplication {
        application {
            configureRouting(VideoManagerInstance, FormManagerInstance)
        }

        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }

        val response = client.get("/${VIDEO_TYPE}/shuffle")
        assertEquals(HttpStatusCode.OK,response.status)
    }

    @Test
    fun testPostVideoByType() = testApplication {

        application {
            configureRouting(VideoManagerInstance, FormManagerInstance)
        }

        val response = client.post("/${VIDEO_TYPE}/add-video") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf(
                "link" to "https://www.youtube.com/watch?v=wBfVsucRe1w",
                "title" to "Chico Buarque - Construção",
                "videoTypes" to VIDEO_TYPE.name,
                "customType" to ""
            ).formUrlEncode())
        }

        assertEquals(HttpStatusCode.Found, response.status)
        assertNotNull(StorageManagerMemoryInstance.videos.find { it.id == "wBfVsucRe1w" })
    }

    @Test
    fun testDeleteVideoByType() = testApplication {
        application {
            configureRouting(VideoManagerInstance, FormManagerInstance)
        }

        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }

        val response = client.get("/${VIDEO_TYPE}/${video3.id}/delete")

        assertEquals(HttpStatusCode.OK,response.status)
        assertNull(StorageManagerMemoryInstance.videos.find { it.id == video3.id })
        assertContains(response.bodyAsText(), "Video deleted!")
    }

    @Test
    fun testGetUpdateVideoByType() = testApplication {
        application {
            configureRouting(VideoManagerInstance, FormManagerInstance)
        }

        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }

        val response = client.get("/${VIDEO_TYPE}/${video1.id}/update")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testPostUpdateVideoByType() = testApplication {
        application {
            configureRouting(VideoManagerInstance, FormManagerInstance)
        }

        val response = client.post("/${VIDEO_TYPE}/${video1.id}/update") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf(
                "title" to "Lo-fi Girl - Christmas",
                "videoTypes" to "NEWS", // necessary to be an existing type in videoTypes.json
                "customType" to ""
            ).formUrlEncode())
        }

        assertEquals(HttpStatusCode.Found, response.status)

        assertEquals(
            "Lo-fi Girl - Christmas",
            StorageManagerMemoryInstance.videos.find { it.id == video1.id }!!.title
        )

        assertEquals(
            "",
            StorageManagerMemoryInstance.videos.find { it.id == video1.id }!!.customTypeName
        )

        assertEquals(
            VideoType.NEWS,
            StorageManagerMemoryInstance.videos.find { it.id == video1.id }!!.videoType
        )

    }

    @Test
    fun testCancelUpdateVideosByType() = testApplication {
        application {
            configureRouting(VideoManagerInstance, FormManagerInstance)
        }

        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }

        val response = client.get("/${VIDEO_TYPE}/${video1.id}/update/cancel")

        assertContains(response.bodyAsText(), actionTitleAdd)
        assertEquals(HttpStatusCode.OK, response.status)
    }

}