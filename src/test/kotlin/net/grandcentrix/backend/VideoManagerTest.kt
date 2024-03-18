package net.grandcentrix.backend

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import net.grandcentrix.backend.models.FormManager.Companion.FormManagerInstance
import net.grandcentrix.backend.models.MusicVideo
import net.grandcentrix.backend.models.NewsVideo
import net.grandcentrix.backend.models.StorageManagerFile.Companion.StorageManagerFileInstance
import net.grandcentrix.backend.models.Video
import net.grandcentrix.backend.models.VideoManager.Companion.VideoManagerInstance
import net.grandcentrix.backend.models.VideoType
import java.io.File
import kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.DeclaredMemberIndex.Empty
import kotlin.test.*

class VideoManagerTest {

    companion object {

        private const val VIDEO_ID = "1YBtzAAChU8"
        private const val VIDEO_TITLE = "Lo-fi Girl - Christmas"
        private const val VIDEO_LINK = "https://www.youtube.com/watch?v=1YBtzAAChU8"
        private const val CUSTOM_TYPE_NAME = "HOLIDAYS"
        val VIDEO_TYPE = VideoType.CUSTOM

        val video1 = Video(
            VIDEO_ID,
            VIDEO_TITLE,
            VIDEO_LINK,
            VIDEO_TYPE,
            CUSTOM_TYPE_NAME
        )

        val video2 = Video(
            "1bvbsx-hpFc",
            "Lo-fi Summer",
            "https://www.youtube.com/watch?v=1bvbsx-hpFc",
            VideoType.NEWS,
            ""
        )

        private const val FILE_NAME = "src/main/resources/testFile.json"
        val videos = mutableListOf(video1,video2)
        val videosJson = Json.encodeToJsonElement(videos).toString()
    }

    @BeforeTest
    fun beforeTest() {
        File(FILE_NAME).writeText(videosJson)

        mockkObject(FormManagerInstance)
        //NOTE define a storage (memory or file) and mock it -> file chose
        mockkObject(StorageManagerFileInstance, recordPrivateCalls = true)
        every { StorageManagerFileInstance["getFile"]() } returns File(FILE_NAME)
        every { StorageManagerFileInstance.videos } returns videos

        VideoManagerInstance.defineStorage(StorageManagerFileInstance)
        VideoManagerInstance.loadVideosToTypeList(videos)
    }

    @AfterTest
    fun afterTest() {
        unmockkAll()
        File(FILE_NAME).writeText("[]")
    }

    @Test
    fun testGetVideos() {
        val videosList = VideoManagerInstance.getVideos()
        val actual = videosList.find{it.id == video1.id}!!

        assertNotNull(videosList)
        assertEquals(video1.id, actual.id)
        assertEquals(video1.title, actual.title)
        assertEquals(video1.link, actual.link)
        assertEquals(video1.videoType, actual.videoType)
    }

    @Test
    fun testLoadVideosToTypeList() {
        val newVideo = Video(
            "-R0UYHS8A_A",
            "Lo-fi Jazz Test",
            "https://www.youtube.com/watch?v=-R0UYHS8A_A",
            VideoType.MUSIC,
            ""
        )
        videos.add(newVideo)
        VideoManagerInstance.loadVideosToTypeList(videos)
        val customVideo = VideoManagerInstance.getVideosByType(newVideo.videoType.name)
            .find { it.id == newVideo.id }!!

        assertEquals(newVideo.id, customVideo.id)
    }

    @Test
    fun testGetVideosByType() {
        val videosByType = VideoManagerInstance.getVideosByType(video2.videoType.name)
        val actual = videosByType.find{it.id == video2.id}!!

        assertNotNull(videosByType)
        assertEquals(video2.id, actual.id)
        assertEquals(video2.title, actual.title)
        assertEquals(video2.link, actual.link)
        assertEquals(video2.videoType, actual.videoType)
    }

    @Test
    fun testGetVideosByTypeEmptyList() {
        val videos = VideoManagerInstance.getVideosByType("Test") // type doesn't exist
        assertIs<MutableList<Empty>>(videos)
    }

    @Test
    fun testDeleteFromTypeList() {
        VideoManagerInstance.deleteFromTypeList(video1.id, VideoType.CUSTOM)
        val customVideos = VideoManagerInstance.getVideosByType(VideoType.CUSTOM.name)
        assertNull(customVideos.find { it.id == video1.id })
    }

    @Test
    fun testFindVideo() {
        val id = VideoManagerInstance.findVideo(video2.id)?.id
        assertEquals(video2.id, id!!)
        assertNotNull(id)
    }

    @Test
    fun testNullFindVideos() {
        assertEquals(null, VideoManagerInstance.findVideo("12345"))
    }

    @Test
    fun testAddToTypeList() {
        val video = NewsVideo(
            "EeRfSNx5RhE",
            "Test",
            "https://www.youtube.com/watch?v=EeRfSNx5RhE",
            VideoType.NEWS,
            ""
        )
        val newsVideo = VideoManagerInstance.addToTypeList(video)
        val newsVideos = VideoManagerInstance.getVideosByType(VideoType.NEWS.name)
        assertContains(newsVideos, newsVideo)

    }

    @Test
    fun testAddVideo() {
        val id = "0MiR7bC9B5o"
        val title = "test"
        val link = "https://www.youtube.com/watch?v=0MiR7bC9B5o&list=PL6NdkXsPL07KN01gH2vucrHCEyyNmVEx4&index=4&pp=iAQB8AUB"
        val videoType = VideoType.MUSIC
        val newVideo = Video(id, title, link, videoType)

        FormManagerInstance.video = newVideo
        VideoManagerInstance.addVideo()

        val video = VideoManagerInstance.getVideos().find { it.id == id }
        val musicVideo = VideoManagerInstance.getVideosByType(VideoType.MUSIC.name)
            .find { it.id == id }

        assertNotNull(video)
        assertNotNull(musicVideo)
    }

    @Test
    fun testDeleteVideo() {
        VideoManagerInstance.deleteVideo(video1.id)
        val video = VideoManagerInstance.getVideos().find { it.id == video1.id }
        val typeVideo = VideoManagerInstance.getVideosByType(VIDEO_TYPE.name)
            .find { it.id == video1.id }
        assertNull(video)
        assertNull(typeVideo)
    }

    @Test
    fun testUpdateVideo() {
        // updating video1
        val updatedVideoValues = mutableMapOf<String,Any>(
            "id" to video2.id,
            "newTitle" to "Test",
            "newType" to VideoType.MUSIC, // before was NEWS
            "newCustomTypeName" to ""
        )
        every { FormManagerInstance.updatedVideoValues } returns updatedVideoValues

        val previousType = video2.videoType.name

        VideoManagerInstance.updateVideo()

        val videoWithPreviousType = VideoManagerInstance.getVideosByType(previousType)
            .find { it.id == video2.id }

        val updatedVideo = VideoManagerInstance.getVideos()
            .find { it.id == video2.id }!!

        val updatedVideoWithType = VideoManagerInstance.getVideosByType(
            updatedVideo.videoType.name
        )
            .find { it.id == video2.id }

        // assert it's removed from previous type list
        assertNull(videoWithPreviousType)
        // assert video is in the new type list
        assertNotNull(updatedVideoWithType)

        // assert values are updated
        assertEquals(FormManagerInstance.updatedVideoValues["newTitle"], updatedVideo.title)
        assertEquals(FormManagerInstance.updatedVideoValues["newType"], updatedVideo.videoType)
        assertEquals(FormManagerInstance.updatedVideoValues["newCustomTypeName"], updatedVideo.customTypeName)

        // assert video was cast to new type
        assertIs<MusicVideo>(updatedVideoWithType)
    }

    @Test
    fun testUpdateVideoBlankTitle() {
        val updatedVideoValues = mutableMapOf<String,Any>(
            "id" to video2.id,
            "newTitle" to "",
            "newType" to VideoType.NEWS,
            "newCustomTypeName" to ""
        )
        every { FormManagerInstance.updatedVideoValues } returns updatedVideoValues

        VideoManagerInstance.updateVideo()

        val updatedVideo = VideoManagerInstance.getVideos()
            .find { it.id == video2.id }!!

        assertEquals(video2.title, updatedVideo.title)
    }

    @Test
    fun testUpdateVideoTypeCastingFails() {
        val updatedVideoValues = mutableMapOf<String,Any>(
            "id" to video2.id,
            "newTitle" to "",
            "newType" to VideoType.NEWS.name, // should be VideoType not string
            "newCustomTypeName" to ""
        )
        every { FormManagerInstance.updatedVideoValues } returns updatedVideoValues

        assertFailsWith(ClassCastException::class, "") {
            VideoManagerInstance.updateVideo()
        }
    }

    @Test
    fun testUpdateVideoVideoNotFound() {
        val updatedVideoValues = mutableMapOf<String,Any>(
            "id" to "12345", // id not found
            "newTitle" to "Test",
            "newType" to VideoType.MUSIC,
            "newCustomTypeName" to ""
        )
        every { FormManagerInstance.updatedVideoValues } returns updatedVideoValues

        assertFailsWith(NoSuchElementException::class, "") {
            VideoManagerInstance.updateVideo()
        }
    }

    @Test
    fun testShuffle() {
        val randomID = VideoManagerInstance.shuffle()
        val videoID = VideoManagerInstance.getVideos().find {it.id == randomID}
        assertNotNull(videoID)
    }

    @Test
    fun testShuffleByType() {
        val randomID = VideoManagerInstance.shuffleByType(video2.videoType.name) // type that exists in the list
        val videoID = VideoManagerInstance.getVideosByType(video2.videoType.name)
            .find {it.id == randomID}
        assertNotNull(videoID)
    }

}