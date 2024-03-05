package net.grandcentrix.backend

import io.ktor.http.*
import java.net.URL




class InMemoryYouTubeManagerClass private constructor() :
    YouTubeManagerInterface,PlayListInterface {
    companion object {
        val inMemoryYouTubeManagerInstance: InMemoryYouTubeManagerClass = InMemoryYouTubeManagerClass()
    }


    private val playlists = mutableListOf<Playlist>()
    private var currentPlaylistIndex: Int = -1
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

    override fun removeVideo(videoIndex: Int): Boolean {
        val currentPlaylist = getCurrentPlaylist()

        return if (currentPlaylist == null || videoIndex < 0 || videoIndex >= currentPlaylist.videos.size) {
            false
        } else {
            val removedVideo = currentPlaylist.videos.removeAt(videoIndex)
            savePlaylists()
            true
        }
    }


    override fun addVideos(videoId: String, customName: String) {
        youtubeLinks.add(VideoInfo(videoId, customName))
    }


    override fun addVideoToPlaylist(videoId: String, customName: String?, playlistName: String) {
        loadPlaylists()

        val playlists = getAllPlaylists()
        val playlist = playlists.find { it.name == playlistName }

        if (playlist == null) {
            throw IllegalArgumentException("Playlist '$playlistName' not found.")
        } else {
            // Add the video to the playlist
            playlist.videos.add(VideoInfo(videoId, customName ?: ""))
            savePlaylists()
        }
    }

    override fun removeVideoByNumber(videoNumber: Int) {
        if (videoNumber >= 0 && videoNumber < youtubeLinks.size) {
            youtubeLinks.removeAt(videoNumber)
        }
    }

    override fun renameVideo(videoId: String, newCustomName: String): Boolean {
        val video = youtubeLinks.find { it.videoId == videoId }
        return if (video == null) {
            false
        } else {
            video.customName = newCustomName
            true
        }
    }

    override fun saveYouTubeLinksJson() {
        // No operation needed here as we don't want to save anything
    }


    override fun validateVideoUrl(newVideoUrl: String?): Pair<HttpStatusCode, String>? {
        if (newVideoUrl.isNullOrBlank()) {
            return Pair(HttpStatusCode.BadRequest, "URL is required")
        }
        val url = URL(newVideoUrl)

        if (url.host !in listOf("www.youtube.com", "youtube.com")) {
            return Pair(HttpStatusCode.BadRequest, "Invalid YouTube URL: Host is not supported")
        }

        val videoId = url.query?.split("v=")?.get(1)?.split("&")?.get(0)
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
        } else {
            throw IllegalArgumentException("Playlist with name '$oldName' not found.")
        }
    }

    override fun deletePlaylist(playlistName: String) {
        val playlist = playlists.find { it.name == playlistName }
        if (playlist != null) {
            playlists.remove(playlist)
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

    override fun savePlaylistToFile(playlist: Playlist) {
        // No operation needed here as we don't want to save playlists to file
    }

    override fun savePlaylists() {
        // No operation needed here as we don't want to save playlists to file
    }

    override fun loadPlaylists() {
        // No operation needed here as we don't load playlists from file
    }

    // Implement other methods for video management
}
