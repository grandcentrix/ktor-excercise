package net.grandcentrix.backend.models

data class CustomTypeVideo(override val id: String, override var title: String, override val link: String, override var videoType: VideoType = VideoType.CUSTOM) : Video(id, title, link, videoType) {

     companion object {
         var customTypeVideos = mutableListOf<Video>()
     }

}