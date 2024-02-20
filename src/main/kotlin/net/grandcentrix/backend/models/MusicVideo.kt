package net.grandcentrix.backend.models


data class MusicVideo (
    override val id: String,
    override var title: String,
    override val link: String,
    override var videoType: VideoType,
    override var customTypeName: String
): Video(id, title, link, videoType, customTypeName)