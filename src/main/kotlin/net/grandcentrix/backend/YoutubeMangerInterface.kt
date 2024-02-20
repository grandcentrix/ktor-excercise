package net.grandcentrix.backend

interface YouTubeManagerInterface {
    fun getRandomYouTubeVideoUrl(): String
    fun addVideos(videoId: String, customName: String)
    fun removeVideoByNumber(videoNumber: Int)
    fun getYoutubeLinks(): List<VideoInfo>
    fun renameVideo(videoId: String, newCustomName: String): Boolean
    fun saveYouTubeLinks()
    fun addVideoToPlaylist(videoId: String, customName: String?, playlistName: String)
    fun removeVideo(videoIndex: Int): Boolean
}