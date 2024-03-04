package net.grandcentrix.backend

import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class YouTubeManagerTest {

    @Test
    fun testJsonYouTubeManager() {
        // Mocking the YouTubeManager
        val youtubeManager = mockk<JsonYouTubeManagerObjectClass>()

        // Stubbing the getYoutubeLinks() method
        every { youtubeManager.getYoutubeLinks() } returns listOf(VideoInfo("VIDEO_ID_1", "Video 1"))

        // Stubbing the addVideos, removeVideoByNumber, and getRandomYouTubeVideoUrl methods
        every { youtubeManager.addVideos("VIDEO_ID_1", "Video 1") } just Runs
        every { youtubeManager.removeVideoByNumber(0) } just Runs
        every { youtubeManager.getRandomYouTubeVideoUrl() } returns "https://www.youtube.com/embed/random-video-id"

        // Add test data
        youtubeManager.addVideos("VIDEO_ID_1", "Video 1")

        // Test adding a video
        assertTrue(youtubeManager.getYoutubeLinks().size == 1)

        // Test getting a random YouTube video URL
        val randomUrl = youtubeManager.getRandomYouTubeVideoUrl()
        assertTrue(randomUrl.startsWith("https://www.youtube.com/embed/"))

        // Test removing a video
        youtubeManager.removeVideoByNumber(0)
        // assertTrue(youtubeManager.getYoutubeLinks().isEmpty())

        // Verify method invocations
        verify(exactly = 1) { youtubeManager.getYoutubeLinks() }
        verify(exactly = 1) { youtubeManager.addVideos("VIDEO_ID_1", "Video 1") }
        verify(exactly = 1) { youtubeManager.removeVideoByNumber(0) }
        verify(exactly = 1) { youtubeManager.getRandomYouTubeVideoUrl() }
    }



    @Test
    fun testGetYouTubeManagerWithPersistence() {
        val youtubeManager = getYouTubeManager(persistLinks = true)

        // Ensure that the returned manager is an instance of JsonYouTubeManagerObjectClass
        assertTrue(youtubeManager is JsonYouTubeManagerObjectClass)

    }

    @Test
    fun testGetYouTubeManagerWithoutPersistence() {
        val youtubeManager = getYouTubeManager(persistLinks = false)

        // Ensure that the returned manager is an instance of InMemoryYouTubeManagerClass
        assertTrue(youtubeManager is InMemoryYouTubeManagerClass)

    }

    @Test
    fun testLoadAndSaveYouTubeLinks() {
        val youtubeManager = JsonYouTubeManagerObjectClass.JsonYouTubeManagerObjectInstance

        // Add test data
        youtubeManager.addVideos("VIDEO_ID_1", "Video 1")

        // Mocking the save and load operations
        mockkObject(youtubeManager)
        every { youtubeManager.saveYouTubeLinksJson() } returns Unit
        every { youtubeManager.loadYouTubeLinks() } returns Unit
        every { youtubeManager.getYoutubeLinks() } returns listOf(VideoInfo("VIDEO_ID_1", "Video 1"))

        // Test saving and loading youtubeLinks
        youtubeManager.saveYouTubeLinksJson()

        // Create a new instance to simulate a different session or restart
        val newYoutubeManager = JsonYouTubeManagerObjectClass.JsonYouTubeManagerObjectInstance
        newYoutubeManager.loadYouTubeLinks()

        assertEquals(1, newYoutubeManager.getYoutubeLinks().size)

        // Verify method invocations
        verify(exactly = 1) { youtubeManager.saveYouTubeLinksJson() }
        verify(exactly = 1) { youtubeManager.loadYouTubeLinks() }
    }


    @Test
    fun testInMemoryYouTubeManager() {
        val youtubeManager = mockk<InMemoryYouTubeManagerClass>()

        // Stubbing the getYoutubeLinks() method
        every { youtubeManager.getYoutubeLinks() } returns listOf(VideoInfo("VIDEO_ID_1", "Video 1"))

        // Stubbing the addVideos, removeVideoByNumber, and getRandomYouTubeVideoUrl methods
        every { youtubeManager.addVideos("VIDEO_ID_1", "Video 1") } just Runs
        every { youtubeManager.removeVideoByNumber(0) } just Runs
        every { youtubeManager.getRandomYouTubeVideoUrl() } returns "https://www.youtube.com/embed/random-video-id"

        // Add test data
        youtubeManager.addVideos("VIDEO_ID_1", "Video 1")

        // Test adding a video
        assertEquals(1, youtubeManager.getYoutubeLinks().size)

        // Test getting a random YouTube video URL
        val randomUrl = youtubeManager.getRandomYouTubeVideoUrl()
        assertTrue(randomUrl.startsWith("https://www.youtube.com/embed/"))

        // Test removing a video
        youtubeManager.removeVideoByNumber(0)

        // Verify method invocations
        verify(exactly = 1) { youtubeManager.getYoutubeLinks() }
        verify(exactly = 1) { youtubeManager.addVideos("VIDEO_ID_1", "Video 1") }
        verify(exactly = 1) { youtubeManager.removeVideoByNumber(0) }
        verify(exactly = 1) { youtubeManager.getRandomYouTubeVideoUrl() }
    }
}
