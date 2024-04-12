package net.grandcentrix.backend.models

import net.grandcentrix.backend.enums.VideoType

data class NewsVideo (
    override val id: String,
    override var title: String,
    override val link: String,
    override var videoType: VideoType,
    override var customTypeName: String = String()
): Video(id, title, link, videoType, customTypeName)
