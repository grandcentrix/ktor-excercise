package net.grandcentrix.backend

import kotlinx.serialization.Serializable

@Serializable
data class Playlist(var name: String, val videos: MutableList<VideoInfo>)