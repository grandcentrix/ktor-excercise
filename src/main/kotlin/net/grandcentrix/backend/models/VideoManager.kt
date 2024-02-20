package net.grandcentrix.backend.models

import net.grandcentrix.backend.models.FormManager.Companion.FormManagerInstance
import net.grandcentrix.backend.models.StorageManagerMemory.Companion.StorageManagerMemoryInstance

open class VideoManager private constructor(
    private var storage: StorageManagerInterface<MutableList<Video>,MutableList<Video>>,
    private val formManager: FormManager
) : VideoManagerInterface {

    companion object {
        val VideoManagerInstance: VideoManager = VideoManager(StorageManagerMemoryInstance, FormManagerInstance)
        private var musicVideos = mutableListOf<MusicVideo>()
        private var newsVideos = mutableListOf<NewsVideo>()
        private var customTypeVideos = mutableListOf<CustomTypeVideo>()
    }

    override fun defineStorage(storageType: StorageManagerInterface<MutableList<Video>,MutableList<Video>>) {
        storage = storageType
    }

    override fun getVideos(): MutableList<Video> {
        return storage.getContent()
    }

    protected fun Video.toMusicVideo(): MusicVideo? {
        if (this.videoType != VideoType.MUSIC) {
            return null
        }
        return MusicVideo(this.id, this.title, this.link, this.videoType, this.customTypeName)
    }
    protected fun Video.toNewsVideo(): NewsVideo? {
        if (this.videoType != VideoType.NEWS) {
            return null
        }
        return NewsVideo(this.id, this.title, this.link, this.videoType, this.customTypeName)
    }
    protected fun Video.toCustomVideo(): CustomTypeVideo? {
        if (this.videoType != VideoType.CUSTOM) {
            return null
        }
        return CustomTypeVideo(this.id, this.title, this.link, this.videoType, this.customTypeName)
    }

    protected fun Video.toType() = when (this.videoType) {
        VideoType.MUSIC -> MusicVideo(this.id, this.title, this.link, this.videoType, this.customTypeName)
        VideoType.NEWS -> NewsVideo(this.id, this.title, this.link, this.videoType, this.customTypeName)
        VideoType.CUSTOM -> CustomTypeVideo(this.id, this.title, this.link, this.videoType, this.customTypeName)
        else -> Video("", "", "", VideoType.CUSTOM, "")
    }

    override fun loadVideosToType(videos: MutableList<Video>) {
        musicVideos = videos.filter { it.videoType == VideoType.MUSIC }.map { it.toMusicVideo()!! }.toMutableList()
        newsVideos = videos.filter { it.videoType == VideoType.NEWS }.map { it.toNewsVideo()!! }.toMutableList()
        customTypeVideos = videos.filter { it.videoType == VideoType.CUSTOM }.map { it.toCustomVideo()!! }.toMutableList()
    }

    override fun getVideosByType(videoType: String): MutableList<out Video> {
        val assignedType = assignType(videoType)
        return when (assignedType) {
            VideoType.MUSIC -> musicVideos
            VideoType.NEWS -> newsVideos
            VideoType.CUSTOM -> customTypeVideos.filter { it.customTypeName == videoType }.toMutableList()
            else -> mutableListOf()
        }
    }

    private fun deleteFromTypeList(id: String, videoType: VideoType) {
        when (videoType) {
            VideoType.MUSIC -> musicVideos.removeIf { it.id == id }
            VideoType.NEWS -> newsVideos.removeIf { it.id == id }
            VideoType.CUSTOM ->  customTypeVideos.removeIf { it.id == id }
            else -> println("Error on deleting video from type list") //FIXME: throw exception?
        }
    }

    override fun findVideo(id: String): Video? = storage.videos.find { it.id == id }

    private fun assignToTypeList(video: Video): Video {
        when (video) {
            is MusicVideo -> musicVideos.add(video)
            is NewsVideo -> newsVideos.add(video)
            is CustomTypeVideo -> customTypeVideos.add(video)
            else -> println("Video type not found!") //FIXME: throw exception?
        }
        return video
    }

    override fun addVideo() {
        val video = formManager.video
        if (findVideo(video.id) != null) {
            formManager.status = "Video already exists!"
            return
        }

        val videoWithType = video.toType()
        assignToTypeList(videoWithType)

        storage.videos.add(video)
        storage.setContent(storage.videos)
        formManager.status = "Video added!"
    }

    override fun deleteVideo(id: String) {
        val video = findVideo(id)!!
        if (!inputIsValid(id)) {
            return
        }

        // delete video from the lists
        deleteFromTypeList(video.id, video.videoType)
        storage.videos.remove(video)
        storage.updateStorage()
        formManager.status = "Video deleted!"

        // check if video is deleted
        // FIXME is this needed? if there was a problem an exception would be raised
    }

    private fun inputIsValid (id: String): Boolean {
        if (storage.videos.size <= 1) {
            formManager.status = "The list cannot be empty!"
            return false
        }
        val video = findVideo(id)
        if (video === null) {
            formManager.status = "Video not found!"
            return false
        }
        return true
    }

    override fun updateVideo() {
        val newValues = formManager.updatedVideoValues
        val video = storage.videos.single { it.id == newValues["id"] }
        val previousType = video.videoType

        deleteFromTypeList(video.id, previousType) // delete video from previous type list

        video.apply {
            videoType = newValues["newType"] as VideoType
            customTypeName = newValues["newCustomTypeName"].toString()
        }

        storage.updateStorage() // add video to new type list
        formManager.status = "Video updated!"

        if (video.videoType != previousType) {
            assignToTypeList(video.toType())
            return
        }
        assignToTypeList(video)
    }

    // mocking
    override fun shuffle(): String  = storage.videos.map { it.id }.random()

    override fun shuffleByType(videoType: String): String = when (assignType(videoType)) {
        VideoType.MUSIC -> musicVideos
        VideoType.NEWS -> newsVideos
        VideoType.CUSTOM -> customTypeVideos.filter { it.customTypeName == videoType }
        else -> storage.videos
    }.map {
        it.id
    }.random()
}
