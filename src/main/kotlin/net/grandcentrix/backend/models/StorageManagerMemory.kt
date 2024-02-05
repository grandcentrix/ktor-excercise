package net.grandcentrix.backend.models


open class StorageManagerMemory() : StorageManagerInterface {
    companion object {
        val StorageManagerMemoryInstance: StorageManagerMemory = StorageManagerMemory()
        const val videoId = "1YBtzAAChU8"
        const val videoTitle = "Lofi Girl - Christmas"
        const val videoLink = "https://www.youtube.com/watch?v=1YBtzAAChU8&list=PL6NdkXsPL07KN01gH2vucrHCEyyNmVEx4&index=2&pp=iAQB8AUB"
    }

    private var videos = mutableListOf(Video(videoId, videoTitle, videoLink, VideoType.MUSIC))

    override fun listVideos(): MutableList<Video> {
        return this.videos
    }

    override fun updateStorage(videos: MutableList<Video>) {
        this.videos = videos
    }

}