package net.grandcentrix.backend

import java.io.File
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

@Serializable
data class VideoInfo(val videoId: String, val customName: String)
object YouTubeManager {

    private val json = Json {}

    val youtubeLinks = mutableListOf<VideoInfo>()

    fun loadYouTubeLinks() {
        val file = File("youtubeLinks.json")
        if (file.exists()) {
            youtubeLinks.clear()
            val jsonContent = file.readText()
            youtubeLinks.addAll(json.decodeFromString<List<VideoInfo>>(jsonContent))
        }
    }

    fun saveYouTubeLinks() {
        val file = File("youtubeLinks.json")
        val jsonContent = json.encodeToString(youtubeLinks)
        file.writeText(jsonContent)
    }

    fun getRandomYouTubeVideoUrl(): String {
        if (youtubeLinks.isEmpty()) {
            return "https://www.youtube.com/"
        }
        val randomIndex = (0 until youtubeLinks.size).random()
        val videoInfo = youtubeLinks[randomIndex]
        val videoId = videoInfo.videoId

        return "https://www.youtube.com/embed/$videoId"
    }
    fun addVideo(videoId: String, customName: String) {
        youtubeLinks.add(VideoInfo(videoId, customName))
        saveYouTubeLinks()
    }

    fun removeVideoByNumber(videoNumber: Int) {
        if (videoNumber >= 0 && videoNumber < youtubeLinks.size) {
            youtubeLinks.removeAt(videoNumber)
            saveYouTubeLinks()
}
    }
}
