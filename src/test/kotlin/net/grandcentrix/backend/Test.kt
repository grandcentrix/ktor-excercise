package net.grandcentrix.backend

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class YouTubeManagerTest {

    @Test
    fun testJsonYouTubeManager() {
        val youtubeManager = JsonYouTubeManagerObjectClass.JsonYouTubeManagerObjectInstance

        // Add test data
        youtubeManager.addVideos("VIDEO_ID_1", "Video 1")

        // Test adding a video
        assertEquals(1, youtubeManager.getYoutubeLinks().size)

        // Test getting a random YouTube video URL
        val randomUrl = youtubeManager.getRandomYouTubeVideoUrl()
        assertTrue(randomUrl.startsWith("https://www.youtube.com/embed/"))

        // Test removing a video
        youtubeManager.removeVideoByNumber(0)
        assertEquals(0, youtubeManager.getYoutubeLinks().size)
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

        // Test saving and loading youtubeLinks
        youtubeManager.saveYouTubeLinksJson()

        // Create a new instance to simulate a different session or restart
        val newYoutubeManager = JsonYouTubeManagerObjectClass.JsonYouTubeManagerObjectInstance
        newYoutubeManager.loadYouTubeLinks()

        assertEquals(1, newYoutubeManager.getYoutubeLinks().size)
    }

    @Test
    fun testInMemoryYouTubeManager() {
        val youtubeManager = InMemoryYouTubeManagerClass.inMemoryYouTubeManagerInstance

        // Add test data
        youtubeManager.addVideos("VIDEO_ID_1", "Video 1")

        // Test adding a video
        assertEquals(1, youtubeManager.getYoutubeLinks().size)

        // Test getting a random YouTube video URL
        val randomUrl = youtubeManager.getRandomYouTubeVideoUrl()
        assertTrue(randomUrl.startsWith("https://www.youtube.com/embed/"))

        // Test removing a video
        youtubeManager.removeVideoByNumber(0)
        assertEquals(0, youtubeManager.getYoutubeLinks().size)
    }
}