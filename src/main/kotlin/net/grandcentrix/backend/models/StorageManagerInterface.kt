package net.grandcentrix.backend.models

interface StorageManagerInterface<in I, out O> {
    fun getContent(): O
    fun setContent(item: I)
}