package net.grandcentrix.backend.models


open class StorageManagerMemory() : StorageManagerInterface {
    companion object {
        val StorageManagerMemoryInstance: StorageManagerMemory = StorageManagerMemory()
        const val videoId = "1YBtzAAChU8"
        const val videoTitle = "Lofi Girl - Christmas"
        const val videoLink = "https://www.youtube.com/watch?v=1YBtzAAChU8"
    }

    private var videos = mutableListOf(Video(videoId, videoTitle, videoLink, VideoType.MUSIC))

    override fun listVideos(): MutableList<Video> {
        return this.videos
    }

    override fun setVideos(videos: MutableList<Video>) {
        this.videos = videos
    }

}