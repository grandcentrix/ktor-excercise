package net.grandcentrix.backend.models

interface StorageManagerInterface {
    fun listVideos(): MutableList<Video>
    fun setVideos(videos: MutableList<Video>)
}