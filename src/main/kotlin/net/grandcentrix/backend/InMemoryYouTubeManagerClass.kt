package net.grandcentrix.backend

import io.ktor.http.*

class InMemoryYouTubeManagerClass private constructor(
    private val playlistManager: PlaylistManager
) : YouTubeManagerInterface {
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
        playlistManager.loadPlaylists()

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

    override fun saveYouTubeLinks(newVideoUrl: String?): Pair<HttpStatusCode, String> {
        return Pair(HttpStatusCode.Accepted, "")
    }
}
