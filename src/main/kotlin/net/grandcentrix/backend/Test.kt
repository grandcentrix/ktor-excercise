package net.grandcentrix.backend

import kotlinx.serialization.json.Json
import org.junit.Assert
import org.junit.Test
import java.io.File

class YouTubeManagerTest {

    private val json = Json
    @Test
    fun testJsonYouTubeManager() {
        val youtubeManager = JsonYouTubeManagerObjectClass.JsonYouTubeManagerObjectInstance

        // Test adding a video
        youtubeManager.addVideo("videoId1", "CustomName1")
        Assert.assertEquals(1, youtubeManager.getYoutubeLinks().size)

        // Test getting a random YouTube video URL
        val randomUrl = youtubeManager.getRandomYouTubeVideoUrl()
        Assert.assertTrue(randomUrl.startsWith("https://www.youtube.com/embed/"))

        // Test removing a video
        youtubeManager.removeVideoByNumber(0)
        Assert.assertEquals(0, youtubeManager.getYoutubeLinks().size)
    }
}

@Test
fun testInMemoryYouTubeManager() {
    val youtubeManager = InMemoryYouTubeManagerClass.inMemoryYouTubeManagerInstance

    // Test adding a video
    youtubeManager.addVideo("videoId1", "CustomName1")
    Assert.assertEquals(1, youtubeManager.getYoutubeLinks().size)

    // Test getting a random YouTube video URL
    val randomUrl = youtubeManager.getRandomYouTubeVideoUrl()
    Assert.assertTrue(randomUrl.startsWith("https://www.youtube.com/embed/"))

    // Test removing a video
    youtubeManager.removeVideoByNumber(0)
    Assert.assertEquals(0, youtubeManager.getYoutubeLinks().size)
}
}