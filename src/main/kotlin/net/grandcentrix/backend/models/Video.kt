package net.grandcentrix.backend.models

import kotlinx.serialization.Serializable

@Serializable
open class Video(open val id: String, open var title: String, open val link: String, open var videoType: VideoType) {

}
