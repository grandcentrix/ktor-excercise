package net.grandcentrix.backend.models

interface VideoManagerInterface {
    val storeIn: StorageManagerInterface
    var status: String
    fun getVideos(): MutableList<Video>
    fun getVideosByType(videoType: String): MutableList<Video>
    fun findVideo(id: String): Video?
    fun addVideo(id: String, title: String, link: String, videoType: String)
    fun deleteVideo(id: String)
    fun updateVideo(id: String, newTitle: String, newType: VideoType)
    fun updateForm(id: String)
    fun shuffle(): String
}