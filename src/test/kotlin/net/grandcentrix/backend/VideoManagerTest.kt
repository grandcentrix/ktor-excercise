package net.grandcentrix.backend

import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.util.*
import junit.framework.TestCase.assertEquals
import net.grandcentrix.backend.models.StorageManagerMemory
import net.grandcentrix.backend.models.Video
import net.grandcentrix.backend.models.VideoManager
import net.grandcentrix.backend.models.VideoType
import kotlin.test.*

class VideoManagerTest() {

    companion object {
        val VideoManagerInstance: VideoManager = VideoManager(storage = StorageManagerMemory.StorageManagerMemoryInstance)
        const val videoId = "1YBtzAAChU8"
        const val videoTitle = "Lofi Girl - Christmas"
        const val videoLink = "https://www.youtube.com/watch?v=1YBtzAAChU8&list=PL6NdkXsPL07KN01gH2vucrHCEyyNmVEx4&index=2&pp=iAQB8AUB"
        val video = Video(videoId,videoTitle,videoLink, VideoType.MUSIC)
        val videos = mutableListOf(video)

        const val videoStatus = "Testing!"
        var actionTitle = "Add a new video:"
        var buttonAction = "/add-video"
    }

    @Test
    fun testGetVideos() {
        val videosList = VideoManagerInstance.getVideos()
        val other = videosList.find {
                    it.id == video.id &&
                    it.title == video.title &&
                    it.link == video.link &&
                    it.videoType == video.videoType
        }?.id

        assertEquals(video.id, other)
        assertNotNull(videos)
    }

    @Test
    fun testFindVideo() {
        assertEquals(videoId, VideoManagerInstance.findVideo(videoId)?.id ?: Video)
    }

    @Test
    fun testNullFindVideos() {
        assertEquals(null, VideoManagerInstance.findVideo("12345"))
    }

    @Test
    fun testFailGetVideoData() {
        val formParameters = Parameters.build {
            append("link", videoLink)
            append("title", "")
            append("videoTypes", VideoType.MUSIC.name)
        }

        assertFails("Video link and title cannot be blank or video link is not supported!") { VideoManagerInstance.getVideoData(formParameters) }
    }

    @Test
    fun testExceptionGetVideoData() {
        val formParameters = Parameters.build {
            append("link", videoLink)
            append("", videoTitle)
            append("videoTypes", VideoType.MUSIC.name)
        }

        assertFailsWith(MissingRequestParameterException::class, "Parameter is incorrect") {
            VideoManagerInstance.getVideoData(
                formParameters
            )
        }
    }

    @Test
    fun testGetVideoData() {
        val formParameters = Parameters.build {
            append("link", videoLink)
            append("title", videoTitle)
            append("videoTypes", VideoType.MUSIC.name)
        }

        assertIsNot<MissingRequestParameterException>(VideoManagerInstance.getVideoData(formParameters))
    }

    @Test
    fun testGetVideoDataParameters() {
        val formParameters = Parameters.build {
            append("link", videoLink)
            append("title", videoTitle)
            append("videoTypes", VideoType.MUSIC.name)
        }

        val id = formParameters.getOrFail("link").substringAfter("v=").substringBefore("&")
        val link = formParameters.getOrFail("link")
        val title = formParameters.getOrFail("title")
        val videoType = formParameters.getOrFail("videoTypes")
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
        val videoType = VideoType.MUSIC
        val newVideo = Video(id, title, link, videoType)

        VideoManagerInstance.addVideo(newVideo)
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

    @Test
    fun testFailGetUpdatedData() {
        val formParameters = Parameters.build {
            append("title", "")
            append("videoTypes", VideoType.NEWS.name)
        }

        assertFails("Video title cannot be blank!") {
            VideoManagerInstance.getUpdatedData(
                videoId,
                formParameters)
        }
    }

    @Test
    fun testExceptionGetUpdatedData() {
        val formParameters = Parameters.build {
            append("title", "Testing")
            append("type", VideoType.NEWS.name)
        }

        assertFailsWith(MissingRequestParameterException::class, "Parameter is incorrect") {
            VideoManagerInstance.getUpdatedData(
                videoId,
                formParameters
            )
        }
    }

    @Test
    fun testGetUpdatedData() {
        val formParameters = Parameters.build {
            append("title", "Testing")
            append("videoTypes", VideoType.NEWS.name)
        }

        assertIsNot<MissingRequestParameterException>(VideoManagerInstance.getUpdatedData(videoId,formParameters))
    }

    @Test
    fun testUpdateVideo() {
        val newTitle = "Test"
        val newType = VideoType.NEWS.name
        val newVideoType = VideoType.assignType(newType)
        val video = Video(videoId, newTitle, videoLink, newVideoType)

        VideoManagerInstance.updateVideo(videoId, newTitle, newVideoType)

        val updatedVideo = VideoManagerInstance.getVideos().single { it.id == videoId }

        assertEquals(video.title, updatedVideo.title)
        assertEquals(video.videoType, updatedVideo.videoType)
    }

}