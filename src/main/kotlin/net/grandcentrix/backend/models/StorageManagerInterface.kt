package net.grandcentrix.backend.models

interface StorageManagerInterface {
    fun listVideos(): MutableList<Video>
    fun updateStorage(videos: MutableList<Video>)
}