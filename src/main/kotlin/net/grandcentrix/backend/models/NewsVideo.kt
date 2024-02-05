package net.grandcentrix.backend.models

data class NewsVideo (override val id: String, override var title: String, override val link: String, override var videoType: VideoType): Video(id, title, link, videoType) {
    companion object {
        var newsVideos = mutableListOf<Video>()
    }
}