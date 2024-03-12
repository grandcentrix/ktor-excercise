package net.grandcentrix.backend

import io.ktor.http.*

interface YouTubeManagerInterface {
    fun getRandomYouTubeVideoUrl(): String
    fun addVideos(videoId: String, customName: String)
    fun removeVideoByNumber(videoNumber: Int)
    fun getYoutubeLinks(): List<VideoInfo>
    fun renameVideo(videoId: String, newCustomName: String): Boolean
    fun saveYouTubeLinksJson()

    fun addVideoToPlaylist(videoId: String, customName: String?, playlistName: String)
    fun removeVideo(videoIndex: Int): Boolean

    fun validateVideoUrl(newVideoUrl: String?): Pair<HttpStatusCode, String>?
    fun getVideosByTag(tag: String): List<VideoInfo>

    fun addTagToVideo(videoId: String, tagName: String)
    fun getAllTags(): List<String>


}