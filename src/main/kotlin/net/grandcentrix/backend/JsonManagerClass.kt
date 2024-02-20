package net.grandcentrix.backend

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

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
        if (youtubeLinks.isNotEmpty()) {
            val randomIndex = (0 until youtubeLinks.size).random()
            val videoInfo = youtubeLinks[randomIndex]
            val videoId = videoInfo.videoId
            return "https://www.youtube.com/embed/$videoId"
        }
        return "https://www.youtube.com/"
    }

    override fun addVideoToPlaylist(videoId: String, customName: String?, playlistName: String) {
        playlistManager.loadPlaylists()

        val playlists = playlistManager.getAllPlaylists()
        val playlist = playlists.find { it.name == playlistName }

        if (playlist == null) {
            throw IllegalArgumentException("Playlist '$playlistName' not found.")
        } else {
            playlist.videos.add(VideoInfo(videoId, customName ?: ""))
            playlistManager.savePlaylists()
        }
    }



    override fun removeVideo(videoIndex: Int): Boolean {
        val currentPlaylist = playlistManager.getCurrentPlaylist()

        return if (currentPlaylist == null || videoIndex < 0 || videoIndex >= currentPlaylist.videos.size) {
            false
        } else {
            val removedVideo = currentPlaylist.videos.removeAt(videoIndex)
            playlistManager.savePlaylists()
            true
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
        return if (video == null) {
            false
        } else {
            video.customName = newCustomName
            saveYouTubeLinks()
            true
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