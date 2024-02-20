package net.grandcentrix.backend.models

data class CustomTypeVideo(
    override val id: String,
    override var title: String,
    override val link: String,
    override var videoType: VideoType,
    override var customTypeName: String
): Video(id, title, link, videoType, customTypeName)