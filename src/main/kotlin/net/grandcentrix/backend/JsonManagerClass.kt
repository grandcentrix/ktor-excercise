package net.grandcentrix.backend

import io.ktor.http.*
import io.ktor.util.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class JsonYouTubeManagerObjectClass private constructor() :
    YouTubeManagerInterface,PlayListInterface,TagInterface {
    companion object {
        val JsonYouTubeManagerObjectInstance: JsonYouTubeManagerObjectClass = JsonYouTubeManagerObjectClass()
    }

    private val json = Json
    private val playlists = mutableListOf<Playlist>()
    private var currentPlaylistIndex: Int = -1
    private val youtubeLinks = mutableListOf<VideoInfo>()
    private val videosFile = File("src/main/resources/youtubeLinks.json")

    init {
        loadYouTubeLinks()
        loadPlaylists()
    }

    override fun getRandomYouTubeVideoUrl(): String {
        if (youtubeLinks.isNotEmpty()) {
            val randomIndex = (0 until youtubeLinks.size).random()
            val videoInfo = youtubeLinks[randomIndex]
            val videoId = videoInfo.videoId
            return "https://www.youtube.com/embed/$videoId"
        }
        return "https://www.youtube.com/"
    }

    override fun addVideoToPlaylist(videoId: String, customName: String?, playlistName: String) {
        loadPlaylists()

        //FIXME maybe this is unnecessary? I guess you can use the variable from the line 16 directly
        val playlists = getAllPlaylists()
        val playlist = playlists.find { it.name == playlistName }

        // EXAMPLE
//        val playlist = playlists.find { it.name == playlistName }

        if (playlist == null) {
            throw IllegalArgumentException("Playlist '$playlistName' not found.")
        } else {
            playlist.videos.add(VideoInfo(videoId, customName ?: ""))
            savePlaylists()
        }
    }

    override fun removeVideo(videoIndex: Int): Boolean {
        val currentPlaylist = getCurrentPlaylist()

        return if (currentPlaylist == null || videoIndex < 0 || videoIndex >= currentPlaylist.videos.size) {
            false
        } else {
            val removedVideo = currentPlaylist.videos.removeAt(videoIndex)
            val videoId = removedVideo.videoId
            val customName = removedVideo.customName
            println("Removing video: $customName ($videoId)") // Just for debugging
            savePlaylists()
            true
        }
    }


    override fun addVideos(videoId: String, customName: String) {
        youtubeLinks.add(VideoInfo(videoId, customName))
        saveYouTubeLinksJson()
    }

    override fun removeVideoByNumber(videoNumber: Int) {
        if (videoNumber in 0 until youtubeLinks.size) {
            youtubeLinks.removeAt(videoNumber)
            saveYouTubeLinksJson()
        }
    }

    override fun renameVideo(videoId: String, newCustomName: String): Boolean {
        val video = youtubeLinks.find { it.videoId == videoId }
        return if (video != null) {
            video.customName = newCustomName
            saveYouTubeLinksJson()
            true
        } else {
            false
        }
    }

    override fun getYoutubeLinks(): List<VideoInfo> {
        return youtubeLinks
    }

    fun loadYouTubeLinks() {
        if (videosFile.exists()) {
            try {
                val jsonContent = videosFile.readText()
                val videoInfoList = json.decodeFromString<List<VideoInfo>>(jsonContent)
                youtubeLinks.addAll(videoInfoList)
            } catch (e: Exception) {
                println("Error loading YouTube links: ${e.message}")
                e.printStackTrace()
            }
        } else {
            println("YouTube links file not found.")
        }
    }




    override fun saveYouTubeLinksJson() {
        val jsonContent = json.encodeToString(youtubeLinks)
        videosFile.writeText(jsonContent)
    }

    override fun validateVideoUrl(newVideoUrl: String?): Pair<HttpStatusCode, String>? {
        if (newVideoUrl.isNullOrBlank()) {
            return Pair(HttpStatusCode.BadRequest, "URL is required")
        }

        val url = URL(newVideoUrl)

        if (url.host !in listOf("www.youtube.com", "youtube.com")) {
            return Pair(HttpStatusCode.BadRequest, "Invalid YouTube URL: Host is not supported")
        }

        val videoIdPattern = Regex("[?&]v=([^&]+)")
        val matchResult = videoIdPattern.find(newVideoUrl ?: "")

        val videoId = matchResult?.groupValues?.getOrNull(1)

        if (videoId.isNullOrBlank()) {
            return Pair(HttpStatusCode.BadRequest, "Invalid YouTube URL: Video ID not found")
        }

        return null
    }

    override fun createPlaylist(name: String) {
        if (playlists.any { it.name == name }) {
            throw IllegalArgumentException("Playlist with name '$name' already exists.")
        }

        val newPlaylist = Playlist(name, mutableListOf())
        playlists.add(newPlaylist)

        savePlaylists()
    }

    override fun getAllPlaylists(): List<Playlist> {
        return playlists
    }

    override fun switchPlaylist(name: String) {
        val index = playlists.indexOfFirst { it.name == name }
        if (index != -1) {
            currentPlaylistIndex = index
        } else {
            throw IllegalArgumentException("Playlist with name '$name' not found.")
        }
    }

    override fun renamePlaylist(oldName: String, newName: String) {
        val existingPlaylist = playlists.find { it.name == oldName }
        if (existingPlaylist != null) {
            existingPlaylist.name = newName
            val oldFile = File("$oldName.json")
            val newFile = File("$newName.json")
            if (oldFile.exists()) {
                oldFile.renameTo(newFile)
            }
            savePlaylists()
        } else {
            throw IllegalArgumentException("Playlist with name '$oldName' not found.")
        }
    }

    override fun deletePlaylist(playlistName: String) {
        val playlist = playlists.find { it.name == playlistName }
        if (playlist != null) {
            val playlistFile = File("$playlistName.json")
            if (playlistFile.exists()) {
                playlistFile.delete()
            }
            playlists.remove(playlist)
            savePlaylists()
        } else {
            throw IllegalArgumentException("Playlist '$playlistName' not found.")
        }
    }

    override fun getCurrentPlaylist(): Playlist? {
        return if (currentPlaylistIndex != -1 && currentPlaylistIndex < playlists.size) {
            playlists[currentPlaylistIndex]
        } else {
            null
        }
    }

    fun migratePlaylists() {
        val jsonFiles = File(".").listFiles { file ->
            file.isFile && file.extension == "json"
        } ?: return

        jsonFiles.map {
            try {
                val jsonContent = it.readText()
                val playlist: Playlist? = json.decodeFromString<Playlist?>(jsonContent)
                if (playlist == null) {
                    println("Couldn't cast to playlist!")
                }
                val playlistFile = File("src/main/resources/playlists/${it.name}")
                Files.move(it.toPath(), playlistFile.toPath())
            } catch (e: Exception) {
//                e.printStackTrace()
                println("${it.name} ignored")
            }
        }
     }

    override fun savePlaylistToFile(playlist: Playlist) {
        val file = File("src/main/resources/playlists/${playlist.name}.json")
        val jsonContent = json.encodeToString(playlist)
        file.writeText(jsonContent)
    }

    override fun loadPlaylists() {

        migratePlaylists()

        val playlistFiles = File("src/main/resources/playlists").listFiles { file ->
            file.isFile && file.extension == "json"
        } ?: return

        val loadedPlaylists: MutableList<Playlist> = mutableListOf()

        for (file in playlistFiles) {
            try {
                if (file.exists()) {
                    val jsonContent = file.readText()
                    val playlist = json.decodeFromString<Playlist>(jsonContent)
                    // Iterate over the videos in the playlist and ensure custom names are not empty
                    playlist.videos.forEach { videoInfo ->
                        if (videoInfo.customName.isEmpty()) {
                            // If the custom name is empty, try to find the corresponding custom name from youtubeLinks
                            val linkedVideo = youtubeLinks.find { it.videoId == videoInfo.videoId }
                            if (linkedVideo != null && linkedVideo.customName.isNotEmpty()) {
                                videoInfo.customName = linkedVideo.customName
                            }
                        }
                    }
                    loadedPlaylists.add(playlist)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        playlists.clear()
        playlists.addAll(loadedPlaylists)
    }

    override fun savePlaylists() {
        playlists.forEach { savePlaylistToFile(it) }
    }

    override fun addTagToVideo(videoId: String, tagName: String) {
        val video = youtubeLinks.find { it.videoId == videoId }
        video?.tags?.add(tagName)
        saveYouTubeLinksJson()
    }

    override fun removeTagFromVideo(videoId: String, tagName: String) {
        val video = youtubeLinks.find { it.videoId == videoId }
        video?.tags?.remove(tagName)
        saveYouTubeLinksJson()
    }

    override fun getVideosByTag(tagName: String): List<VideoInfo> {
        return youtubeLinks.filter { tagName in it.tags }
    }

        override fun getAllTags(): List<String> {
            return youtubeLinks.flatMap { it.tags }.distinct()
    }
    }



