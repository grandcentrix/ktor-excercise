package net.grandcentrix.backend

import java.io.File
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

@Serializable
data class VideoInfo(val videoId: String, val customName: String)

/**
 * Interface to describe the functionalities of YouTubeManager.
 */
interface YouTubeManagerInterface {

    /**
     * Loads YouTube links from a file.
     */
    fun loadYouTubeLinks()

    /**
     * Saves YouTube links to a file.
     */
    fun saveYouTubeLinks()

    /**
     * Retrieves a random YouTube video URL.
     *
     * @return A random YouTube video URL.
     */
    fun getRandomYouTubeVideoUrl(): String

    /**
     * Adds a video to the YouTube links.
     *
     * @param videoId The ID of the video to be added.
     * @param customName A custom name for the video.
     */
    fun addVideo(videoId: String, customName: String)

    /**
     * Removes a video from the YouTube links based on its position.
     *
     * @param videoNumber The position of the video to be removed.
     */
    fun removeVideoByNumber(videoNumber: Int)
}

object YouTubeManager : YouTubeManagerInterface {

    private val json = Json {}

    val youtubeLinks = mutableListOf<VideoInfo>()

    override fun loadYouTubeLinks() {
        val file = File("youtubeLinks.json")
        if (file.exists()) {
            youtubeLinks.clear()
            val jsonContent = file.readText()
            youtubeLinks.addAll(json.decodeFromString<List<VideoInfo>>(jsonContent))
        }
    }

    override fun saveYouTubeLinks() {
        val file = File("youtubeLinks.json")
        val jsonContent = json.encodeToString(youtubeLinks)
        file.writeText(jsonContent)
    }

    override fun getRandomYouTubeVideoUrl(): String {
        if (youtubeLinks.isEmpty()) {
            return "https://www.youtube.com/"
        }
        val randomIndex = (0 until youtubeLinks.size).random()
        val videoInfo = youtubeLinks[randomIndex]
        val videoId = videoInfo.videoId

        return "https://www.youtube.com/embed/$videoId"
    }

    override fun addVideo(videoId: String, customName: String) {
        youtubeLinks.add(VideoInfo(videoId, customName))
        saveYouTubeLinks()
    }

    override fun removeVideoByNumber(videoNumber: Int) {
        if (videoNumber >= 0 && videoNumber < youtubeLinks.size) {
            youtubeLinks.removeAt(videoNumber)
            saveYouTubeLinks()
        }
    }
}