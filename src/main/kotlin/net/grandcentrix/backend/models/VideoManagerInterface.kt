package net.grandcentrix.backend.models

import io.ktor.http.*

interface VideoManagerInterface {
    val storeIn: StorageManagerInterface
    var status: String
    fun getVideos(): MutableList<Video>
    fun loadVideosToType(videos: MutableList<Video>)
    fun getVideosByType(videoType: String): MutableList<Video>
    fun findVideo(id: String): Video?
    fun getVideoData(formParameters: Parameters)
    fun addVideo(id: String, title: String, link: String, videoType: String)
    fun deleteVideo(id: String)
    fun getUpdatedData(id: String, formParameters: Parameters)
    fun updateVideo(id: String, newTitle: String, newType: VideoType)
    fun updateForm(id: String)
    fun shuffle(): String
    fun shuffleByType(videoType: String): String
}