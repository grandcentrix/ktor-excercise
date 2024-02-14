package net.grandcentrix.backend.models

data class NewsVideo (override val id: String, override var title: String, override val link: String, override var videoType: VideoType = VideoType.NEWS): Video(id, title, link, VideoType.NEWS) {
    companion object {
        var newsVideos = mutableListOf<Video>()
    }
}
