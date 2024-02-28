package net.grandcentrix.backend.models

import io.ktor.server.plugins.*
import net.grandcentrix.backend.models.FormManager.Companion.FormManagerInstance
import net.grandcentrix.backend.models.StorageManagerMemory.Companion.StorageManagerMemoryInstance
import net.grandcentrix.backend.models.StorageManagerTypesFile.Companion.StorageManagerTypesFileInstance

open class VideoManager private constructor (
    private var storage: StorageManagerInterface<List<Video>,List<Video>>,
    private val formManager: FormManager
) : VideoManagerInterface {

    companion object {
        val VideoManagerInstance: VideoManager =
            VideoManager(StorageManagerMemoryInstance, FormManagerInstance)
        private var musicVideos = mutableListOf<MusicVideo>()
        private var newsVideos = mutableListOf<NewsVideo>()
        private var customTypeVideos = mutableListOf<CustomTypeVideo>()
    }

    override fun defineStorage (
        storageType: StorageManagerInterface<List<Video>,List<Video>>
    ) {
        storage = storageType
    }

    override fun getVideos(): List<Video> {
//        deleteEmptyCustomVideoType()
        return storage.getContent()
    }

    protected fun Video.toType() = when (this.videoType) {
        VideoType.MUSIC -> MusicVideo(
            this.id, this.title, this.link, this.videoType, this.customTypeName
        )

        VideoType.NEWS -> NewsVideo(
            this.id, this.title, this.link, this.videoType, this.customTypeName
        )

        VideoType.CUSTOM -> CustomTypeVideo(
            this.id, this.title, this.link, this.videoType, this.customTypeName
        )

        else -> Video("", "", "", VideoType.CUSTOM, "")
    }

    override fun loadVideosToTypeList(videos: List<Video>) {
        videos.map { it ->
            when (it.videoType) {
                VideoType.MUSIC -> musicVideos = videos.mapNotNull {
                    it.toType() as? MusicVideo }.toMutableList()

                VideoType.NEWS -> newsVideos = videos.mapNotNull {
                    it.toType() as? NewsVideo }.toMutableList()

                VideoType.CUSTOM -> customTypeVideos = videos.mapNotNull {
                    it.toType() as? CustomTypeVideo }.toMutableList()

                else -> throw NotImplementedError("Videos wasn't added to type. Video type wasn't probably found!")
            }
        }
    }

    override fun getVideosByType(videoType: String): MutableList<out Video> {
        val assignedType = assignType(videoType)
        return when (assignedType) {
            VideoType.MUSIC -> musicVideos
            VideoType.NEWS -> newsVideos
            VideoType.CUSTOM -> customTypeVideos.filter {
                it.customTypeName == videoType
            }.toMutableList()
            else -> mutableListOf()
        }
    }

     fun deleteFromTypeList(id: String, videoType: VideoType) {
        when (videoType) {
            VideoType.MUSIC -> musicVideos.removeIf { it.id == id }
            VideoType.NEWS -> newsVideos.removeIf { it.id == id }
            VideoType.CUSTOM ->  customTypeVideos.removeIf { it.id == id }
            else -> throw NotFoundException("Video not deleted. Type not found!")
        }
    }

    override fun findVideo(id: String): Video? = storage.videos.find { it.id == id }

     fun addToTypeList(video: Video): Video {
        when (video) {
            is MusicVideo -> musicVideos.add(video)
            is NewsVideo -> newsVideos.add(video)
            is CustomTypeVideo -> customTypeVideos.add(video)
            else -> throw ClassCastException("Video type not found!")
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
        addToTypeList(videoWithType)

        storage.videos.add(video)
        storage.updateStorage()
        formManager.status = "Video added!"
    }

    override fun deleteVideo(id: String) {
        val video = findVideo(id)!!
        if (!inputIsValid(id)) {
            return
        }

        // delete video from the lists
        deleteFromTypeList(video.id, video.videoType)
        storage.removeItem(video)
        formManager.status = "Video deleted!"
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
        val updatedVideoValues = formManager.updatedVideoValues
        val video = storage.videos.single { it.id == updatedVideoValues["id"] } //TODO catch exception?
        val previousType = video.videoType
        val newTitle = updatedVideoValues["newTitle"].toString()

        deleteFromTypeList(video.id, previousType) // delete video from previous type list

        video.apply {
            videoType = updatedVideoValues["newType"] as VideoType //TODO catch exception?
            customTypeName = updatedVideoValues["newCustomTypeName"].toString()
            if(newTitle.isNotBlank()) {
                title = newTitle
            }
        }

        storage.updateStorage() // add video to new type list
        formManager.status = "Video updated!"

        if (video.videoType != previousType) {
            addToTypeList(video.toType())
            return
        }
        addToTypeList(video)
    }

    override fun shuffle(): String  = storage.getContent().map { it.id }.random()

    override fun shuffleByType(videoType: String): String =
        when (assignType(videoType)) {
            VideoType.MUSIC -> musicVideos
            VideoType.NEWS -> newsVideos
            VideoType.CUSTOM -> customTypeVideos.filter {
                it.customTypeName == videoType
            }
        }.map {
        it.id
    }.random()

    fun deleteEmptyCustomVideoType() {
        formManager.videoTypes.map{
            if (getVideosByType(it).isEmpty()) {
                StorageManagerTypesFileInstance.removeItem(it)
            }
        }
    }
}
