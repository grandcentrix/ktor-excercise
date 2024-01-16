package net.grandcentrix.backend

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

@Serializable
data class VideoInfo(val videoId: String, val customName: String)

interface YouTubeManagerInterface {

    fun getRandomYouTubeVideoUrl(): String

    fun addVideo(videoId: String, customName: String)

    fun removeVideoByNumber(videoNumber: Int)

    fun getYoutubeLinks(): List<VideoInfo>
}

object YouTubeManagerConfig {
    var useJsonManager: Boolean = true
}

class JsonYouTubeManagerObjectClass private constructor() : YouTubeManagerInterface {
    companion object {
        val JsonYouTubeManagerObjectInstance: JsonYouTubeManagerObjectClass = JsonYouTubeManagerObjectClass()
    }

    private val json = Json
    private val youtubeLinks = mutableListOf<VideoInfo>()

    override fun getYoutubeLinks(): List<VideoInfo> {
        return youtubeLinks
    }

    fun loadYouTubeLinks() {
        val file = java.io.File("youtubeLinks.json")
        if (file.exists()) {
            youtubeLinks.clear()
            val jsonContent = file.readText()
            youtubeLinks.addAll(json.decodeFromString<List<VideoInfo>>(jsonContent))
        }
    }

    fun saveYouTubeLinks() {
        val file = java.io.File("youtubeLinks.json")
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

class InMemoryYouTubeManagerClass private constructor() : YouTubeManagerInterface {
    companion object {
        val inMemoryYouTubeManagerInstance: InMemoryYouTubeManagerClass = InMemoryYouTubeManagerClass()
    }

    private val json = Json
    private val youtubeLinks = mutableListOf<VideoInfo>()

    override fun getYoutubeLinks(): List<VideoInfo> {
        return youtubeLinks
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
    }

    override fun removeVideoByNumber(videoNumber: Int) {
        if (videoNumber >= 0 && videoNumber < youtubeLinks.size) {
            youtubeLinks.removeAt(videoNumber)
        }
    }
}

