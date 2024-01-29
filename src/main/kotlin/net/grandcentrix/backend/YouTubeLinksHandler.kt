import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.Serializable
import java.io.File

interface YouTubeManagerInterface {
    fun getRandomYouTubeVideoUrl(): String
    fun addVideo(videoId: String, customName: String, addToUserPlaylist: Boolean)
    fun removeVideoByNumber(videoNumber: Int)
    fun getYoutubeLinks(): List<VideoInfo>
    fun renameVideo(videoId: String, newCustomName: String): Boolean
    fun addVideoToPlaylist(videoId: String, customName: String, addToUserPlaylist: Boolean)
    fun getUserPlaylist(): List<VideoInfo>
}

@Serializable
data class VideoInfo(val videoId: String, var customName: String)

class JsonYouTubeManagerObjectClass private constructor() : YouTubeManagerInterface {
    companion object {
        val JsonYouTubeManagerObjectInstance: JsonYouTubeManagerObjectClass = JsonYouTubeManagerObjectClass()
    }

    private val json = Json
    private val youtubeLinks = mutableListOf<VideoInfo>()
    private val userPlaylist = mutableListOf<VideoInfo>()

    init {
        loadYouTubeLinks()
        loadUserPlaylist()
    }

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

    override fun addVideo(videoId: String, customName: String, addToUserPlaylist: Boolean) {
        youtubeLinks.add(VideoInfo(videoId, customName))
        if (addToUserPlaylist) {
            userPlaylist.add(VideoInfo(videoId, customName))
        }
        saveYouTubeLinks()
        if (addToUserPlaylist) {
            saveUserPlaylist()
        }
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

    override fun addVideoToPlaylist(videoId: String, customName: String, addToUserPlaylist: Boolean) {
        if (addToUserPlaylist) {
            userPlaylist.add(VideoInfo(videoId, customName))
            saveUserPlaylist()
        }
    }

    override fun getUserPlaylist(): List<VideoInfo> {
        return userPlaylist
    }

     fun loadYouTubeLinks() {
        val file = File("youtubeLinks.json")
        if (file.exists()) {
            youtubeLinks.clear()
            val jsonContent = file.readText()
            youtubeLinks.addAll(json.decodeFromString<List<VideoInfo>>(jsonContent))
        }
    }

     fun loadUserPlaylist() {
        val file = File("userPlaylist.json")
        if (file.exists()) {
            userPlaylist.clear()
            val jsonContent = file.readText()
            userPlaylist.addAll(json.decodeFromString<List<VideoInfo>>(jsonContent))
        }
    }

     fun saveYouTubeLinks() {
        val file = File("youtubeLinks.json")
        val jsonContent = json.encodeToString(youtubeLinks)
        file.writeText(jsonContent)
    }

    fun saveUserPlaylist() {
        val file = File("userPlaylist.json")
        val jsonContent = json.encodeToString(userPlaylist)
        file.writeText(jsonContent)
    }
}

class InMemoryYouTubeManagerClass private constructor(): YouTubeManagerInterface {
    companion object {
        val inMemoryYouTubeManagerInstance : InMemoryYouTubeManagerClass = InMemoryYouTubeManagerClass()
    }

    private val youtubeLinks = mutableListOf<VideoInfo>()
    private val userPlaylist = mutableListOf<VideoInfo>()

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

    override fun addVideo(videoId: String, customName: String, addToUserPlaylist: Boolean) {
        youtubeLinks.add(VideoInfo(videoId, customName))
        if (addToUserPlaylist) {
            userPlaylist.add(VideoInfo(videoId, customName))
        }
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

    override fun addVideoToPlaylist(videoId: String, customName: String, addToUserPlaylist: Boolean) {
        userPlaylist.add(VideoInfo(videoId, customName))
    }

    override fun getUserPlaylist(): List<VideoInfo> {
        return userPlaylist
    }
}
