package net.grandcentrix.backend

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File
import java.lang.Thread.sleep

interface YouTubeManagerInterface {
    fun getRandomYouTubeVideoUrl(): String
    fun addVideos(videoId: String, customName: String)
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
        playlistManager.loadPlaylists()
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
        // Attempt to create the playlist if it doesn't exist
        try {
            playlistManager.createPlaylist(playlistName)
        } catch (e: IllegalArgumentException) {
            // Playlist already exists, continue
        }
        val playlists = playlistManager.getAllPlaylists()
        val playlist = playlists.find { it.name == playlistName }
        if (playlist != null) {
            // Add the video to the playlist
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




    override fun addVideos(videoId: String, customName: String) {
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

    override fun addVideos(videoId: String, customName: String) {
        youtubeLinks.add(VideoInfo(videoId, customName))
        saveYouTubeLinks()
    }


    override fun addVideoToPlaylist(videoId: String, customName: String?, playlistName: String) {
        // Attempt to create the playlist if it doesn't exist
        try {
            playlistManager.createPlaylist(playlistName)
        } catch (e: IllegalArgumentException) {
            // Playlist already exists, continue
        }

        // Print available playlists for debugging
        val playlists = playlistManager.getAllPlaylists()
        println("Available playlists:")
        playlists.forEach { println(it.name) }

        // Find the playlist to add the video to
        val playlist = playlists.find { it.name == playlistName }
        if (playlist != null) {
            // Add the video to the playlist
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
    private val playlists = mutableListOf<Playlist>()
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

        // Print the name of the playlist we are attempting to create
        println("Creating playlist: $name")

        // Create a new playlist and add it to the list
        val newPlaylist = Playlist(name, mutableListOf())
        playlists.add(newPlaylist)

        // Print the playlists after adding the new one
        println("Playlists after adding new playlist:")
        playlists.forEach { println(it.name) }

        // Save the new playlist to file immediately
        println("Saving new playlist to file...")
        savePlaylists()

        // Print a message indicating successful creation
        println("Playlist $name created.")
    }





    fun getAllPlaylists(): List<Playlist> {
        return playlists
    }

    fun switchPlaylist(name: String) {
        val index = playlists.indexOfFirst { it.name == name }
        if (index != -1) {
            currentPlaylistIndex = index
            println("Switch to: $name")
        } else {
            throw IllegalArgumentException("Playlist with name '$name' not found.")
        }
    }

    fun renamePlaylist(oldName: String, newName: String) {
        val playlist = playlists.find { it.name == oldName } ?: throw IllegalArgumentException("Playlist with name '$oldName' not found.")
        playlist.name = newName
        savePlaylists()
    }

    fun deletePlaylist(playlistName: String) {
        val playlist = playlists.find { it.name == playlistName }
        if (playlist != null) {
            val playlistFile = File("$playlistName.json")
            if (playlistFile.exists()) {
                // Delete the playlist file from the file system
                playlistFile.delete()
                // Remove the playlist from the in-memory list
                playlists.remove(playlist)
                // Save the updated playlists immediately
                savePlaylists()
                println("Playlist '$playlistName' deleted successfully.")
            } else {
                println("Playlist file '$playlistName.json' not found.")
            }
        } else {
            throw IllegalArgumentException("Playlist '$playlistName' not found.")
        }
    }



    fun getCurrentPlaylist(): Playlist? {
        return if (currentPlaylistIndex != -1 && currentPlaylistIndex < playlists.size) {
            playlists[currentPlaylistIndex]
        } else {
            null
        }
    }

    private fun savePlaylistToFile(playlist: Playlist) {
        val file = File("${playlist.name}.json")
        val jsonContent = json.encodeToString(playlist)
        file.writeText(jsonContent)
        println("Playlist '${playlist.name}' saved successfully to ${file.name}")
    }


    fun savePlaylists() {
        playlists.forEach { savePlaylistToFile(it) }
    }

    fun loadPlaylists() {
        val playlistFiles = File(".").listFiles { file ->
            file.isFile && file.extension == "json"
        } ?: return

        val loadedPlaylists: MutableList<Playlist> = mutableListOf()
        println("Loading playlists:")
        for (file in playlistFiles) {
            try {
                val jsonContent = file.readText()
                val playlist = json.decodeFromString<Playlist>(jsonContent)
                loadedPlaylists.add(playlist)
                println("Playlist '${playlist.name}' loaded successfully from ${file.name}")
            } catch (e: Exception) {
                println("Failed to load playlist from ${file.name}: ${e.message}")
            }
        }
        playlists.clear()
        playlists.addAll(loadedPlaylists)
    }
}