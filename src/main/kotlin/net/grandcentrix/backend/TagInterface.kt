package net.grandcentrix.backend

interface TagInterface {
    fun addTagToVideo(videoId: String, tagName: String)
    fun removeTagFromVideo(videoId: String, tagName: String)
    fun getVideosByTag(tagName: String): List<VideoInfo>

}