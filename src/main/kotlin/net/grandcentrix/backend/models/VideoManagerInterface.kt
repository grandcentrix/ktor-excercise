package net.grandcentrix.backend.models

interface VideoManagerInterface {

    fun defineStorage(storageType: StorageManagerInterface<MutableList<Video>,MutableList<Video>>)
    fun getVideos(): MutableList<Video>
    fun loadVideosToType(videos: MutableList<Video>)
    fun getVideosByType(videoType: String): MutableList<out Video>
    fun findVideo(id: String): Video?
    fun addVideo()
    fun deleteVideo(id: String)
    fun updateVideo()
    fun shuffle(): String
    fun shuffleByType(videoType: String): String

}