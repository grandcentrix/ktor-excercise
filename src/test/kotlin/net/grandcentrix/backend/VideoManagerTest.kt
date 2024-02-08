package net.grandcentrix.backend

import junit.framework.TestCase.assertEquals
import net.grandcentrix.backend.models.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class VideoManagerTest() {

    companion object {
        val VideoManagerInstance: VideoManager = VideoManager(
            storage = StorageManagerMemory.StorageManagerMemoryInstance,
            formManager = FormManager.FormManagerInstance
        )

        const val videoId = "1YBtzAAChU8"
        const val videoTitle = "Lofi Girl - Christmas"
        const val videoLink = "https://www.youtube.com/watch?v=1YBtzAAChU8"
        const val videoStatus = "Testing!"

        val video = Video(videoId,videoTitle,videoLink, VideoType.MUSIC)
        val videos = mutableListOf(video)

    }

    @Test
    fun testGetVideos() {
        val videosList = VideoManagerInstance.getVideos()
        val actual = videosList.first()

        assertNotNull(videos)
        assertEquals(video.id, actual.id)
        assertEquals(video.title, actual.title)
        assertEquals(video.link, actual.link)
        assertEquals(video.videoType, actual.videoType)
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