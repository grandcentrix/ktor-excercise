package net.grandcentrix.backend.models

open class StorageManagerMemory() : StorageManagerInterface<MutableList<Video>,MutableList<Video>> {
    companion object {
        val StorageManagerMemoryInstance: StorageManagerMemory = StorageManagerMemory()
        const val VIDEO_ID = "1YBtzAAChU8"
        const val VIDEO_TITLE = "Lo-fi Girl - Christmas"
        const val VIDEO_LINK = "https://www.youtube.com/watch?v=1YBtzAAChU8"
    }

    override var videos = mutableListOf(Video(VIDEO_ID, VIDEO_TITLE, VIDEO_LINK, VideoType.MUSIC))

    override fun getContent(): MutableList<Video> {
        return videos
    }

    override fun setEntry(item: Video) {
        videos.add(item)
    }

    override fun removeEntry(item: Video) {
       videos.remove(item)
    }

//    override fun updateStorage() {
////        videos[index] = item
//    }

    override fun setContent(list: MutableList<Video>) {
        videos = list
    }
}