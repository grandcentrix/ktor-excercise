package net.grandcentrix.backend.models

interface StorageManagerInterface<in I, out O> {
    val videos: MutableList<Video>
        get() = mutableListOf()

    fun getContent(): O
    fun getEntry(item: Video) {}
    fun setEntry(item: Video) {}
    fun removeEntry(item: Video) {}
    fun updateStorage() {}
    fun setContent(list: I)
}