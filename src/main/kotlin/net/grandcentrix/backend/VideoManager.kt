package net.grandcentrix.backend

import net.grandcentrix.backend.FormManager.Companion.FormManagerInstance
import net.grandcentrix.backend.StorageManagerMemory.Companion.StorageManagerMemoryInstance
import net.grandcentrix.backend.enums.VideoType
import net.grandcentrix.backend.enums.assignType
import net.grandcentrix.backend.interfaces.StorageManagerInterface
import net.grandcentrix.backend.models.CustomTypeVideo
import net.grandcentrix.backend.models.MusicVideo
import net.grandcentrix.backend.models.NewsVideo
import net.grandcentrix.backend.models.Video

open class VideoManager private constructor (
    private var storage: StorageManagerInterface<List<Video>, List<Video>>,
    private val formManager: FormManager
) {

    companion object {
        val VideoManagerInstance: VideoManager =
            VideoManager(StorageManagerMemoryInstance, FormManagerInstance)
        private val musicVideos = mutableListOf<MusicVideo>()
        private val newsVideos = mutableListOf<NewsVideo>()
        private val customTypeVideos = mutableListOf<CustomTypeVideo>()
    }

    fun defineStorage(storageType: StorageManagerInterface<List<Video>, List<Video>>) {
        storage = storageType
    }

    fun getVideos(): List<Video> {
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
    }

    fun loadVideosToTypeList(videos: List<Video>) {
        videos.forEach {
            when (it.videoType) {
                VideoType.MUSIC ->  {
                    val musicVideo = it.toType() as? MusicVideo
                        ?: throw VideoTypeCastingException("Wrong video type casting")
                    musicVideos.add(musicVideo)
                }

                VideoType.NEWS -> {
                    val newsVideo = it.toType() as? NewsVideo
                        ?: throw VideoTypeCastingException("Wrong video type casting")
                    newsVideos.add(newsVideo)
                }

                VideoType.CUSTOM -> {
                    val customVideo = it.toType() as? CustomTypeVideo
                        ?: throw VideoTypeCastingException("Wrong video type casting")
                    customTypeVideos.add(customVideo)
                }
            }
        }
    }

    class VideoTypeCastingException(override val message: String?) : Exception()

    fun getVideosByType(videoType: String): MutableList<out Video> {
        return when (val assignedType = assignType(videoType)) {
            VideoType.MUSIC -> musicVideos
            VideoType.NEWS -> newsVideos
            VideoType.CUSTOM -> customTypeVideos.filter {
                it.videoType == assignedType
            }.toMutableList()
        }
    }

    fun deleteFromTypeList(id: String, videoType: VideoType) {
        when (videoType) {
            VideoType.MUSIC -> musicVideos.removeIf { it.id == id }
            VideoType.NEWS -> newsVideos.removeIf { it.id == id }
            VideoType.CUSTOM -> customTypeVideos.removeIf { it.id == id }
        }
    }

    fun findVideo(id: String): Video? = storage.videos.find { it.id == id }

    fun <T> addToTypeList(video: T): T {
        when (video) {
            is MusicVideo -> musicVideos.add(video)
            is NewsVideo -> newsVideos.add(video)
            is CustomTypeVideo -> customTypeVideos.add(video)
            else -> throw ClassCastException("Video type not found!")
        }
        return video
    }

    fun addVideo() {
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

    fun deleteVideo(id: String) {
        inputIsValid(id)

        val video = findVideo(id)
        // delete video from the lists
        deleteFromTypeList(video!!.id, video.videoType)
        storage.removeItem(video)
        formManager.status = "Video deleted!"
    }

    private fun inputIsValid(id: String) {
        val video = findVideo(id)
        if (video == null) {
            formManager.status = "Video not found!"
            throw NoSuchElementException("Video not found")
        }
    }

    fun updateVideo() {
        val updatedVideoValues = formManager.updatedVideoValues
        val video = storage.videos.single { it.id == updatedVideoValues["id"] }
        val previousType = video.videoType
        val newTitle = updatedVideoValues["newTitle"].toString()

        deleteFromTypeList(video.id, previousType) // delete video from previous type list

        video.apply {
            videoType = updatedVideoValues["newType"] as VideoType
            customTypeName = updatedVideoValues["newCustomTypeName"].toString()
            if (newTitle.isNotBlank()) {
                title = newTitle
            }
        }

        storage.updateStorage() // add video to new type list
        formManager.status = "Video updated!"

        addToTypeList(video.toType())
    }

    fun shuffle(): String {
        if (storage.getContent().isEmpty()) {
            throw IllegalStateException("No videos found - storage is empty!")
        }
        return storage.getContent().map { it.id }.random()
    }

    fun shuffleByType(videoType: String): String {
        if (getVideosByType(videoType).isEmpty()) {
            throw IllegalStateException("No videos found - storage is empty!")
        }

        return when (assignType(videoType)) {
            VideoType.MUSIC -> musicVideos
            VideoType.NEWS -> newsVideos
            VideoType.CUSTOM -> customTypeVideos.filter {
                it.customTypeName == videoType
            }
        }.map {
            it.id
        }.random()
    }

//    private fun deleteEmptyCustomVideoType() {
//        formManager.videoTypes.map{
//            if (getVideosByType(it).isEmpty()) {
//                StorageManagerTypesFileInstance.removeItem(it)
//            }
//            return
//        }
//    }
}
