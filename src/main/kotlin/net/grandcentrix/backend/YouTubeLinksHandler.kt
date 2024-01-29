package net.grandcentrix.backend

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

@Serializable
data class VideoInfo(val videoId: String, var customName: String)



/**
 * Interface to describe the functionalities of YouTubeManager.
 */
interface YouTubeManagerInterface {

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

    fun getYoutubeLinks(): List<VideoInfo>

    fun renameVideo(videoId: String, newCustomName: String): Boolean

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

    override fun renameVideo(videoId: String, newCustomName: String): Boolean {
        val video = youtubeLinks.find { it.videoId == videoId }
        return if (video != null) {
            video.customName = newCustomName
            saveYouTubeLinks()
            true
        } else {
            false
        }
    }
}


class  InMemoryYouTubeManagerClass private constructor(): YouTubeManagerInterface {

    companion object {
        val  inMemoryYouTubeManagerInstance : InMemoryYouTubeManagerClass  = InMemoryYouTubeManagerClass()
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

    override fun renameVideo(videoId: String, newCustomName: String): Boolean {
        val video = youtubeLinks.find { it.videoId == videoId }
        return if (video != null) {
            video.customName = newCustomName
            true
        } else {
            false
        }
    }
    }




