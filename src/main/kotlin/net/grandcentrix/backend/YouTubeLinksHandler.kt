package net.grandcentrix.backend

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File

interface YouTubeManagerInterface {
    fun getRandomYouTubeVideoUrl(): String
    fun addVideo(videoId: String, customName: String, playlistName: String)

    fun removeVideoByNumber(videoNumber: Int)
    fun getYoutubeLinks(): List<VideoInfo>
    fun renameVideo(videoId: String, newCustomName: String): Boolean
    fun saveYouTubeLinks()

    fun addVideoToPlaylist(videoId: String, customName: String?, playlistName: String)

    fun removeVideo(videoId: String): Boolean

}

@Serializable
data class VideoInfo(val videoId: String, var customName: String)

class JsonYouTubeManagerObjectClass private constructor(private val playlistManager: PlaylistManager) : YouTubeManagerInterface {
    companion object {
        val JsonYouTubeManagerObjectInstance: JsonYouTubeManagerObjectClass = JsonYouTubeManagerObjectClass(PlaylistManager())
    }

    private val json = Json
    private val youtubeLinks = mutableListOf<VideoInfo>()

    init {
        loadYouTubeLinks()
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

    override fun addVideoToPlaylist(videoId: String, customName: String?, playlistName: String) {
        // Print available playlists for debugging
        println("Available playlists:")
        playlistManager.getAllPlaylists().forEach { println(it.name) }

        val playlist = playlistManager.getAllPlaylists().find { it.name == playlistName }
        if (playlist != null) {
            playlist.videos.add(VideoInfo(videoId, customName ?: ""))
            playlistManager.savePlaylists()
        } else {
            throw IllegalArgumentException("Playlist '$playlistName' not found.")
        }
    }


    override fun removeVideo(videoId: String): Boolean {
        val video = youtubeLinks.find { it.videoId == videoId }
        return if (video != null) {
            youtubeLinks.remove(video)
            saveYouTubeLinks()
            true
        } else {
            false
        }
    }

    override fun addVideo(videoId: String, customName: String, playlistName: String) {
        val playlist = playlistManager.getAllPlaylists().find { it.name == playlistName }
        if (playlist != null) {
            playlist.videos.add(VideoInfo(videoId, customName))
            playlistManager.savePlaylists()
        } else {
            throw IllegalArgumentException("Playlist '$playlistName' not found.")
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

    override fun getYoutubeLinks(): List<VideoInfo> {
        return youtubeLinks
    }

    fun loadYouTubeLinks() {
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
}

class InMemoryYouTubeManagerClass private constructor(private val playlistManager: PlaylistManager) : YouTubeManagerInterface {
    companion object {
        val inMemoryYouTubeManagerInstance: InMemoryYouTubeManagerClass = InMemoryYouTubeManagerClass(PlaylistManager())
    }

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

    override fun removeVideo(videoId: String): Boolean {
        val video = youtubeLinks.find { it.videoId == videoId }
        return if (video != null) {
            youtubeLinks.remove(video)
            saveYouTubeLinks()
            true
        } else {
            false
        }
    }

    override fun addVideo(videoId: String, customName: String, playlistName: String) {
        val playlist = playlistManager.getAllPlaylists().find { it.name == playlistName }
        if (playlist != null) {
            playlist.videos.add(VideoInfo(videoId, customName))
            playlistManager.savePlaylists()
        } else {
            throw IllegalArgumentException("Playlist '$playlistName' not found.")
        }
    }


    override fun addVideoToPlaylist(videoId: String, customName: String?, playlistName: String) {
        // Print available playlists for debugging
        println("Available playlists:")
        playlistManager.getAllPlaylists().forEach { println(it.name) }

        val playlist = playlistManager.getAllPlaylists().find { it.name == playlistName }
        if (playlist != null) {
            playlist.videos.add(VideoInfo(videoId, customName ?: ""))
            playlistManager.savePlaylists()
        } else {
            throw IllegalArgumentException("Playlist '$playlistName' not found.")
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

    override fun saveYouTubeLinks() {
        // No operation needed here as we don't want to save anything
    }
}


@Serializable
data class Playlist(var name: String, val videos: MutableList<VideoInfo>)

class PlaylistManager {
     val playlists = mutableListOf<Playlist>()
     var currentPlaylistIndex: Int = -1
    private val json = Json
    init {
        loadPlaylists()
    }

    fun createPlaylist(name: String) {
        // Check if the playlist with the given name already exists
        if (playlists.any { it.name == name }) {
            throw IllegalArgumentException("Playlist with name '$name' already exists.")
        }

        // Create a new playlist and add it to the list
        playlists.add(Playlist(name, mutableListOf()))
        savePlaylistToFile(name)
        println("Playlist $name created.")
    }

    fun getAllPlaylists(): List<Playlist> {
        return playlists
    }

    fun switchPlaylist(name: String) {
        val index = playlists.indexOfFirst { it.name == name }
        if (index != -1) {
            currentPlaylistIndex = index
        } else {
            throw IllegalArgumentException("Playlist with name '$name' not found.")
        }
    }

    fun renamePlaylist(oldName: String, newName: String) {
        val playlist = playlists.find { it.name == oldName } ?: throw IllegalArgumentException("Playlist with name '$oldName' not found.")
        playlist.name = newName
    }

    fun deletePlaylist(playlistName: String, playlistIndex: Int) {
        val playlist = playlists.getOrNull(playlistIndex) ?: throw IllegalArgumentException("Playlist at index $playlistIndex not found.")
        if (playlist.name == playlistName) {
            playlists.removeAt(playlistIndex)
        } else {
            throw IllegalArgumentException("Playlist at index $playlistIndex does not match the provided name '$playlistName'.")
        }
    }

    fun getCurrentPlaylist(): Playlist? {
        return if (currentPlaylistIndex != -1 && currentPlaylistIndex < playlists.size) {
            playlists[currentPlaylistIndex]
        } else {
            null
        }
    }

    private fun savePlaylistToFile(name: String) {
        val playlist = playlists.find { it.name == name } ?: return
        val file = File("$name.json")
        val jsonContent = json.encodeToString(playlist)
        file.writeText(jsonContent)
        println("Playlist '$name' saved successfully to ${file.name}")
    }

    fun savePlaylists() {
        playlists.forEach { savePlaylistToFile(it.name) }
    }
    fun loadPlaylists() {
        val playlistFiles = File(".").listFiles { file ->
            file.isFile && file.extension == "json"
        } ?: return

        println("Loading playlists:")
        for (file in playlistFiles) {
            try {
                val jsonContent = file.readText()
                val playlist = json.decodeFromString<Playlist>(jsonContent)
                playlists.add(playlist)
                println("Playlist '${playlist.name}' loaded successfully from ${file.name}")
            } catch (e: Exception) {
                println("Failed to load playlist from ${file.name}: ${e.message}")
            }
        }
    }
}


