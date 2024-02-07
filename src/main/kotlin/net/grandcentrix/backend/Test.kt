package net.grandcentrix.backend



import org.junit.Assert
import org.junit.Test
import java.io.File

class YouTubeManagerTest {

    @Test
    fun testJsonYouTubeManager() {
        val youtubeManager = JsonYouTubeManagerObjectClass.JsonYouTubeManagerObjectInstance

        // Test adding a video
        youtubeManager.addVideo("videoId1", "CustomName1", "abd")
        Assert.assertEquals(1, youtubeManager.getYoutubeLinks().size)

        // Test getting a random YouTube video URL
        val randomUrl = youtubeManager.getRandomYouTubeVideoUrl()
        Assert.assertTrue(randomUrl.startsWith("https://www.youtube.com/embed/"))

        // Test removing a video
        youtubeManager.removeVideoByNumber(0)
        Assert.assertEquals(0, youtubeManager.getYoutubeLinks().size)

    }

    @Test
    fun testGetYouTubeManagerWithPersistence() {
        val youtubeManager = getYouTubeManager(persistLinks = true)

        // Ensure that the returned manager is an instance of JsonYouTubeManagerObjectClass
        Assert.assertTrue(youtubeManager is JsonYouTubeManagerObjectClass)
    }

    @Test
    fun testGetYouTubeManagerWithoutPersistence() {
        val youtubeManager = getYouTubeManager(persistLinks = false)

        // Ensure that the returned manager is an instance of InMemoryYouTubeManagerClass
        Assert.assertTrue(youtubeManager is InMemoryYouTubeManagerClass)
    }
    @Test
    fun testLoadAndSaveYouTubeLinks() {
        val youtubeManager = JsonYouTubeManagerObjectClass.JsonYouTubeManagerObjectInstance
        // Test saving and loading youtubeLinks
        youtubeManager.addVideo("videoId1", "CustomName1", "dsv")
        youtubeManager.saveYouTubeLinks()

        // Create a new instance to simulate a different session or restart
        val newYoutubeManager = JsonYouTubeManagerObjectClass.JsonYouTubeManagerObjectInstance
        newYoutubeManager.loadYouTubeLinks()

        Assert.assertEquals(1, newYoutubeManager.getYoutubeLinks().size)
    }
    @Test
    fun testInMemoryYouTubeManager() {
        val youtubeManager = InMemoryYouTubeManagerClass.inMemoryYouTubeManagerInstance

        // Test adding a video
        youtubeManager.addVideo("videoId1", "CustomName1", "dsf")
        Assert.assertEquals(1, youtubeManager.getYoutubeLinks().size)

        // Test getting a random YouTube video URL
        val randomUrl = youtubeManager.getRandomYouTubeVideoUrl()
        Assert.assertTrue(randomUrl.startsWith("https://www.youtube.com/embed/"))

        // Test removing a video
        youtubeManager.removeVideoByNumber(0)
        Assert.assertEquals(0, youtubeManager.getYoutubeLinks().size)
    }
}


