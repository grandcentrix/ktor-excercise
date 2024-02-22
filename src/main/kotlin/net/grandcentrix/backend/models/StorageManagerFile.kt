package net.grandcentrix.backend.models

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.io.File

open class StorageManagerFile(): StorageManagerInterface<MutableList<Video>,MutableList<Video>> {

    companion object {
        val StorageManagerFileInstance: StorageManagerFile = StorageManagerFile()
        private const val FILE_NAME = "src/main/resources/videosList.json"
    }

    override val videos = this.getContent()

    fun getFile() = File(FILE_NAME)

    //TODO change mutable list to list
    override fun getContent(): MutableList<Video> {
        val fileText = getFile().readText()
        if (fileText != "[]") {
            val videosList = Json.decodeFromString<MutableList<Video>>(fileText)
            return videosList
        }
        return mutableListOf()
    }

    override fun setContent(list: MutableList<Video>) {
        val videosJson = Json.encodeToJsonElement(list).toString()
        getFile().writeText(videosJson)
    }

    override fun setItem(item: Video) {
        videos.add(item)
        setContent(videos)
    }

    override fun removeItem(item: Video) {
        videos.remove(item)
        setContent(videos)
    }

    override fun updateStorage() {
        setContent(videos)
    }

}