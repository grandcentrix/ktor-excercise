package net.grandcentrix.backend.models

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.io.File

open class StorageManagerTypesFile(): StorageManagerInterface<MutableList<String>,MutableList<String>> {
    companion object {
        val StorageManagerTypesFileInstance: StorageManagerTypesFile = StorageManagerTypesFile()
    }

    private fun getFile(): File {
        val fileName = "src/main/resources/videoTypes.json"
        val file = File(fileName)
        return file
    }

    override fun getContent(): MutableList<String> {
        val fileText = getFile().readText()
        if (fileText != "[]") {
            val videoTypes = Json.decodeFromString<MutableList<String>>(fileText)
            return videoTypes
        }
        return mutableListOf()
    }

    override fun setContent(item: MutableList<String>) {
        val videoTypes = Json.encodeToJsonElement(item).toString()
        getFile().writeText(videoTypes)
    }

}