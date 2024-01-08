package net.grandcentrix.backend

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class VideoInfo(val videoId: String, val customName: String)

val json = Json {}

val youtubeLinks = mutableListOf<VideoInfo>()
