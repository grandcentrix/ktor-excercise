package net.grandcentrix.backend.models

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.io.File

open class StorageManagerTypesFile: StorageManagerInterface<List<String>,List<String>> {
    companion object {
        val StorageManagerTypesFileInstance: StorageManagerTypesFile = StorageManagerTypesFile()
        private const val FILE_NAME = "src/main/resources/videoTypes.json"
    }

    private fun getFile() = File(FILE_NAME)

    override fun getContent(): List<String> {
        val fileText = getFile().readText()
        if (fileText == "[]") {
            return emptyList()
        }
        return Json.decodeFromString<List<String>>(fileText)
    }

     fun removeItem(item: String) {
        val types = getContent().toMutableList()
        types.remove(item)
        setContent(types)
    }

    override fun setContent(list: List<String>) {
        val videoTypes = Json.encodeToJsonElement(list).toString()
        getFile().writeText(videoTypes)
    }

}