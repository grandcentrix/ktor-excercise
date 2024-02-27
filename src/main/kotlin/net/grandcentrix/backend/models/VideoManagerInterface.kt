package net.grandcentrix.backend.models

interface VideoManagerInterface {

    fun defineStorage(storageType: StorageManagerInterface<List<Video>,List<Video>>)
    fun getVideos(): List<Video>
    fun loadVideosToTypeList(videos: List<Video>)
    fun getVideosByType(videoType: String): MutableList<out Video>
    fun findVideo(id: String): Video?
    fun addVideo()
    fun deleteVideo(id: String)
    fun updateVideo()
    fun shuffle(): String
    fun shuffleByType(videoType: String): String

}