package net.grandcentrix.backend

import kotlinx.serialization.Serializable

@Serializable
data class VideoInfo(
    val videoId: String,
    var customName: String,
    val tags: MutableList<String> = mutableListOf()
)