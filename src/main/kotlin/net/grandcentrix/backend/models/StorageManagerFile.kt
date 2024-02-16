package net.grandcentrix.backend.models

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.io.File

open class StorageManagerFile(): StorageManagerInterface<MutableList<Video>,MutableList<Video>> {

    companion object {
        val StorageManagerFileInstance: StorageManagerFile = StorageManagerFile()

    }

    override val videos = this.getContent()

    fun getFile(): File {
        val fileName = "src/main/resources/videosList.json"
        val file = File(fileName)
        return file
    }

    override fun setEntry(item: Video) {
        videos.add(item)
        setContent(videos)
    }

    override fun removeEntry(item: Video) {
        videos.remove(item)
        setContent(videos)
    }

    override fun updateStorage() {
//        videos[index] = item
        setContent(videos)
    }

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

}