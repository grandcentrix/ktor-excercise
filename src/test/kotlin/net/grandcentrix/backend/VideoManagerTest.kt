package net.grandcentrix.backend

import io.mockk.mockkObject
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import net.grandcentrix.backend.models.CustomTypeVideo
import net.grandcentrix.backend.models.FormManager.Companion.FormManagerInstance
import net.grandcentrix.backend.models.NewsVideo
import net.grandcentrix.backend.models.StorageManagerFile.Companion.StorageManagerFileInstance
import net.grandcentrix.backend.models.Video
import net.grandcentrix.backend.models.VideoManager.Companion.VideoManagerInstance
import net.grandcentrix.backend.models.VideoType
import org.junit.Before
import java.io.File
import kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.DeclaredMemberIndex.Empty
import kotlin.test.*

class VideoManagerTest() {

    companion object {
        const val VIDEO_ID = "1YBtzAAChU8"
        const val VIDEO_TITLE = "Lo-fi Girl - Christmas"
        const val VIDEO_LINK = "https://www.youtube.com/watch?v=1YBtzAAChU8"
        val VIDEO_TYPE = VideoType.CUSTOM
        val video = Video(
            VIDEO_ID,
            VIDEO_TITLE,
            VIDEO_LINK,
            VIDEO_TYPE,
            "HOLIDAYS"
        ) // attributes according to a video in the json file

        val typeNames = VideoType.entries.map { it.name }.toMutableList()
        private const val FILE_NAME = "src/main/resources/testFile.json"
    }

    @Before
    fun beforeTests() {
        val video = Video(
            "1YBtzAAChU8",
            "Test",
            "https://www.youtube.com/watch?v=1YBtzAAChU8",
            VideoType.MUSIC,
            ""
        )
        val videosJson = Json.encodeToJsonElement(video).toString()
        File(FILE_NAME).writeText(videosJson)

        //NOTE define a storage (memory or file) and mock it
        mockkObject(StorageManagerFileInstance) //using file storage in this case
        mockkObject(FormManagerInstance)
    }

    @AfterTest
    fun afterTest() {
        unmockkAll()
        File(FILE_NAME).writeText("[]")
    }

    @Test
    fun testDefineStorage() {

    }

    @Test
    fun testGetVideos() {
        val videosList = VideoManagerInstance.getVideos()
        val actual = videosList.find{it.id == video.id}!!

        assertNotNull(videosList)
        assertEquals(video.id, actual.id)
        assertEquals(video.title, actual.title)
        assertEquals(video.link, actual.link)
        assertEquals(video.videoType, actual.videoType)
    }

    @Test
    fun testLoadVideosToType() {
        val videos = mutableListOf(video)
        VideoManagerInstance.loadVideosToType(videos)
        val customVideos = VideoManagerInstance.getVideosByType(VIDEO_TYPE.name)
        assertContains(customVideos, video)
    }

    @Test
    fun testGetVideosByType() {
        val customVideos = VideoManagerInstance.getVideosByType(VIDEO_TYPE.name)
        val actual = customVideos.find{it.id == video.id}!!

        assertNotNull(customVideos)
        assertEquals(video.id, actual.id)
        assertEquals(video.title, actual.title)
        assertEquals(video.link, actual.link)
        assertEquals(video.videoType, actual.videoType)
    }

    @Test
    fun testGetVideosByTypeEmptyList() {
        val videos = VideoManagerInstance.getVideosByType("Test") // type doesn't exist
        assertIs<MutableList<Empty>>(videos)
    }

    @Test
    fun testDeleteFromTypeList() {
        VideoManagerInstance.deleteFromTypeList(VIDEO_ID, VideoType.CUSTOM)
        val customVideos = VideoManagerInstance.getVideosByType(VideoType.CUSTOM.name)
        assertContains(customVideos, video)
    }

    @Test
    fun testFindVideo() {
        assertEquals(VIDEO_ID, VideoManagerInstance.findVideo(VIDEO_ID)?.id ?: Video)
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
        val newsVideo = VideoManagerInstance.addToTypeList(video) //FIXME private function
        val newsVideos = VideoManagerInstance.getVideosByType(VideoType.NEWS.name)
        assertContains(newsVideos, newsVideo)

    }

    @Test
    fun testAddToTypeListNotFound() {
        val video = Video(
            "EeRfSNx5RhE",
            "Test",
            "https://www.youtube.com/watch?v=EeRfSNx5RhE",
            VideoType.NEWS,
            ""
        )

        val newsVideo = VideoManagerInstance.addToTypeList(video)
        val newsVideos = VideoManagerInstance.getVideosByType(VideoType.NEWS.name)
//        //TODO assert exception/error
    }

    @Test
    fun testAddVideo() {
        val id = "0MiR7bC9B5o"
        val title = "test"
        val link = "https://www.youtube.com/watch?v=0MiR7bC9B5o&list=PL6NdkXsPL07KN01gH2vucrHCEyyNmVEx4&index=4&pp=iAQB8AUB"
        val videoType = VideoType.MUSIC
        val newVideo = Video(id, title, link, videoType)

        FormManagerInstance.video = newVideo
        mockkObject(VideoManagerInstance.addVideo())

        val video = VideoManagerInstance.getVideos().find { it.id == id }
        val musicVideo = VideoManagerInstance.getVideosByType(VideoType.MUSIC.name)
            .find { it.id == id }

        assertNotNull(video)
        assertNotNull(musicVideo)
    }

    @Test
    fun testDeleteVideo() {
        VideoManagerInstance.deleteVideo(VIDEO_ID)
        val video = VideoManagerInstance.getVideos().find { it.id == VIDEO_ID }
        val typeVideo = VideoManagerInstance.getVideosByType(VIDEO_TYPE.name)
            .find { it.id == VIDEO_ID }
        assertNull(video)
        assertNull(typeVideo)
    }

    //TODO test inputIsValid?

    @Test
    fun testUpdateVideo() {
        FormManagerInstance.updatedVideoValues = mutableMapOf(
            "id" to VIDEO_ID,
            "newTitle" to "Test",
            "newType" to VideoType.NEWS,
            "newCustomTypeName" to ""
        )

        val typeVideo = CustomTypeVideo(
            VIDEO_ID,
            VIDEO_TITLE,
            VIDEO_LINK,
            VIDEO_TYPE,
            "HOLIDAYS"
        )

        mockkObject(VideoManagerInstance.updateVideo())

        val videoWithPreviousType = VideoManagerInstance.getVideosByType(VIDEO_TYPE.name)
            .find { it.id == VIDEO_ID }

        val updatedVideo = VideoManagerInstance.getVideos()
            .find { it.id == VIDEO_ID }!!

        val updatedVideoWithType = VideoManagerInstance.getVideosByType(updatedVideo.videoType.name)
            .find { it.id == VIDEO_ID }

        // assert it's removed from previous type list
        assertNull(videoWithPreviousType)
        // assert video is in the new type list
        assertNotNull(updatedVideoWithType)

        // assert values are updated
        assertEquals(FormManagerInstance.updatedVideoValues["newTitle"], updatedVideo.title)
        assertEquals(FormManagerInstance.updatedVideoValues["newType"], updatedVideo.videoType)
        assertEquals(FormManagerInstance.updatedVideoValues["newCustomTypeName"], updatedVideo.customTypeName)

        // assert video was cast to new type
        assertIs<NewsVideo>(updatedVideoWithType)
    }

    //TODO fail cases for update video

    @Test
    fun testShuffle() {
        val randomID = VideoManagerInstance.shuffle()
        val videoID = VideoManagerInstance.getVideos().find {it.id == randomID}
        assertNotNull(videoID)
    }

    @Test
    fun testShuffleByType() {
        val randomID = VideoManagerInstance.shuffleByType(VideoType.MUSIC.name)
        val videoID = VideoManagerInstance.getVideosByType(VideoType.MUSIC.name)
            .find {it.id == randomID}
        assertNotNull(videoID)
    }

}