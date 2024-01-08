
package net.grandcentrix.backend

import java.io.File
import java.net.URL
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

fun loadYouTubeLinks() {
    val file = File("youtubeLinks.json")
    if (file.exists()) {
        youtubeLinks.clear()
        val jsonContent = file.readText()
        youtubeLinks.addAll(json.decodeFromString<List<VideoInfo>>(jsonContent))
    }
}

fun saveYouTubeLinks() {
    val file = File("youtubeLinks.json")
    val jsonContent = json.encodeToString(youtubeLinks)
    file.writeText(jsonContent)
}
