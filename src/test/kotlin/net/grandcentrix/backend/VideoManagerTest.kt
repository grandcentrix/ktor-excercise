package net.grandcentrix.backend

import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.util.*
import junit.framework.TestCase.assertEquals
import net.grandcentrix.backend.models.StorageManagerMemory
import net.grandcentrix.backend.models.Video
import net.grandcentrix.backend.models.VideoManager
import net.grandcentrix.backend.models.VideoType
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class VideoManagerTest() {

    companion object {
        val VideoManagerInstance: VideoManager = VideoManager(storage = StorageManagerMemory.StorageManagerMemoryInstance)
        val videoId = "1YBtzAAChU8"
        val videoTitle = "Lofi Girl - Christmas"
        val videoLink = "https://www.youtube.com/watch?v=1YBtzAAChU8&list=PL6NdkXsPL07KN01gH2vucrHCEyyNmVEx4&index=2&pp=iAQB8AUB"
        val videos = mutableListOf(Video(videoId,videoTitle,videoLink, VideoType.MUSIC))
        val videoStatus = "Testing!"
        var actionTitle = "Add a new video:"
        var buttonAction = "/add-video"
    }

    @Test
    fun testGetVideos() {
        assertIs<MutableList<Video>>(VideoManagerInstance.getVideos())
        assertNotNull(VideoManagerInstance.getVideos())
    }

    @Test
    fun testFindVideo() {
        assertEquals(null, VideoManagerInstance.findVideo(""))
        assertEquals(videoId, VideoManagerInstance.findVideo(videoId)?.id ?: Video)
    }

    @Test(expected = MissingRequestParameterException::class)
    fun testGetVideoData() {
        val formParameters = Parameters.build {
            append("link", videoLink)
            append("title", videoTitle)
            append("videoType", VideoType.MUSIC.name)
        }

        val id = formParameters.getOrFail("link").substringAfter("v=").substringBefore("&")
        val link = formParameters.getOrFail("link")
        val title = formParameters.getOrFail("title")
        val videoType = formParameters.getOrFail("videoType")
        val testMissingParameter = formParameters.getOrFail("test")


        assertEquals(MissingRequestParameterException::class, testMissingParameter)

        assertEquals(videoId, id)
        assertEquals(videoTitle, title)
        assertEquals(videoLink, link)
        assertEquals(videoType, VideoType.MUSIC.name)
    }

    @Test
    fun testAddVideo() {
        val id = "0MiR7bC9B5o"
        val title = "test"
        val link = "https://www.youtube.com/watch?v=0MiR7bC9B5o&list=PL6NdkXsPL07KN01gH2vucrHCEyyNmVEx4&index=4&pp=iAQB8AUB"
        val videoType = VideoType.MUSIC.name

        VideoManagerInstance.addVideo(id, title, link, videoType)
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

    @Test(expected = MissingRequestParameterException::class)
    fun testGetUpdatedData() {
        val formParameters = Parameters.build {
            append("link", videoLink)
            append("title", "Testing")
            append("videoType", VideoType.NEWS.name)
        }

        val newTitle = formParameters.getOrFail("title")
        val newType = formParameters.getOrFail("videoType")
        val testMissingParameter = formParameters.getOrFail("")

        assertEquals("Testing", newTitle)
        assertEquals(VideoType.NEWS.name, newType)
        assertEquals(MissingRequestParameterException::class, testMissingParameter)
    }

    @Test
    fun testUpdateVideo() {
        val newTitle = "Test"
        val newType = VideoType.NEWS.name

        val newVideoType = VideoType.assignType(newType)
        VideoManagerInstance.updateVideo(videoId, newTitle, newVideoType)
        val updatedVideo = VideoManagerInstance.getVideos().single { it.id == videoId }
        val video = Video(videoId, newTitle, videoLink, newVideoType)

        assertEquals(video.title, updatedVideo.title)
        assertEquals(video.videoType, updatedVideo.videoType)
    }

}