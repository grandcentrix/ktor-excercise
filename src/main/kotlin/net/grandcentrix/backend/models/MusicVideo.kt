package net.grandcentrix.backend.models

data class MusicVideo(override val id: String, override var title: String, override val link: String, override var videoType: VideoType = VideoType.MUSIC): Video(id, title, link, videoType) {
    companion object {
        var musicVideos = mutableListOf<Video>()
    }
}