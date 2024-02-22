package net.grandcentrix.backend.models

interface StorageManagerInterface<in I, out O> {

    val videos: MutableList<Video>
        get() = mutableListOf()

    fun getContent(): O
    fun getItem(item: Video) {}
    fun setItem(item: Video) {}
    fun removeItem(item: Video) {}
    fun updateStorage() {}
    fun setContent(list: I)
}