package net.grandcentrix.backend

import io.ktor.http.*

interface YouTubeManagerInterface {
    fun getRandomYouTubeVideoUrl(): String
    fun addVideos(videoId: String, customName: String)
    fun removeVideoByNumber(videoNumber: Int)
    fun getYoutubeLinks(): List<VideoInfo>
    fun renameVideo(videoId: String, newCustomName: String): Boolean
    fun saveYouTubeLinks()
    fun saveYouTubeLinks(newVideoUrl: String?): Pair<HttpStatusCode, String>
    fun addVideoToPlaylist(videoId: String, customName: String?, playlistName: String)
    fun removeVideo(videoId: String): Boolean
}