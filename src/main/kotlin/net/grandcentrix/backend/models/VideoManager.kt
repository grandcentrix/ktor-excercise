package net.grandcentrix.backend.models;

import net.grandcentrix.backend.models.CustomTypeVideo.Companion.customTypeVideos
import net.grandcentrix.backend.models.FormManager.Companion.FormManagerInstance
import net.grandcentrix.backend.models.MusicVideo.Companion.musicVideos
import net.grandcentrix.backend.models.NewsVideo.Companion.newsVideos
import net.grandcentrix.backend.models.StorageManagerMemory.Companion.StorageManagerMemoryInstance

open class VideoManager(var storage: StorageManagerInterface<MutableList<Video>,MutableList<Video>>, val formManager: FormManager) : VideoManagerInterface {
    companion object {
        val VideoManagerInstance: VideoManager = VideoManager(StorageManagerMemoryInstance, FormManagerInstance)
    }

    override fun defineStorage(storageType: StorageManagerInterface<MutableList<Video>,MutableList<Video>>) {
        storage = storageType
    }

    override fun getVideos(): MutableList<Video> {
        return storage.getContent()
    }

    override fun loadVideosToType(videos: MutableList<Video>) {
        musicVideos = videos.filter { it.videoType == VideoType.MUSIC }.toMutableList()
        newsVideos = videos.filter { it.videoType == VideoType.NEWS }.toMutableList()
//        gameVideos = videos.filter { it.videoType == VideoType.GAME }.toMutableList()
//        eduVideos = videos.filter { it.videoType == VideoType.EDU }.toMutableList()
//        docVideos = videos.filter { it.videoType == VideoType.DOC }.toMutableList()
//        liveVideos = videos.filter { it.videoType == VideoType.LIVE }.toMutableList()

        customTypeVideos = videos.filter { it.videoType == VideoType.CUSTOM}.toMutableList()
        print(customTypeVideos)
    }

    override fun getVideosByType(videoType: String): MutableList<Video> {
        val assignedType = assignType(videoType)
        return when (assignedType) {
            VideoType.MUSIC -> musicVideos
            VideoType.NEWS -> newsVideos
    //            VideoType.DOC -> return docVideos
    //            VideoType.EDU -> return eduVideos
    //            VideoType.GAME -> return gameVideos
    //            VideoType.LIVE -> return liveVideos
            VideoType.CUSTOM -> customTypeVideos.filter { it.customTypeName == videoType }.toMutableList()
            else -> mutableListOf()
        }
    }

    private fun deleteFromTypeList(id: String, videoType: VideoType, customType: String) {
        when (videoType) {
            VideoType.MUSIC -> musicVideos.removeIf { it.id == id }
            VideoType.NEWS -> newsVideos.removeIf { it.id == id }
//            VideoType.GAME -> gameVideos.removeIf { it.id == id }
//            VideoType.EDU -> eduVideos.removeIf { it.id == id }
//            VideoType.DOC -> docVideos.removeIf { it.id == id }
//            VideoType.LIVE -> liveVideos.removeIf { it.id == id }
            VideoType.CUSTOM ->  customTypeVideos.removeIf { it.id == id }
            else -> println("Error on deleting video from type list")
        }
        print(customTypeVideos)
    }

    override fun findVideo(id: String): Video? {
        val video = storage.videos.find { it.id == id }
        return video
    }

    private fun assignToTypeList(video: Video): Video {
        when (video.videoType) {
            VideoType.MUSIC -> musicVideos.add(video)
            VideoType.NEWS -> newsVideos.add(video)
//            VideoType.GAME -> TODO()
//            VideoType.EDU -> TODO()
//            VideoType.DOC -> TODO()
//            VideoType.LIVE -> TODO()
            VideoType.CUSTOM -> customTypeVideos.add(video)
            else -> println("Video type not found!")
        }
        print(customTypeVideos)
        return video
    }

    override fun addVideo() {
        val video = formManager.video
        if (findVideo(video.id) == null) {
            storage.setEntry(video)
            assignToTypeList(video)
            formManager.status = "Video added!"
        } else {
            formManager.status = "Video already exists!"
        }
    }

    override fun deleteVideo(id: String) {
        if (storage.videos.size > 1) {
            val video = findVideo(id)

            // delete video from the lists
            if (video !== null) {
                deleteFromTypeList(video.id, video.videoType, video.customTypeName)
                storage.removeEntry(video)
            } else {
                formManager.status = "Video not found!"
            }

            // check if video is deleted
            if (findVideo(id) == null) {
                formManager.status = "Video deleted!"
            } else {
                formManager.status = "Oops, there was some problem while deleting!"
            }
        } else {
            formManager.status = "The list cannot be empty!"
        }
    }

    override fun updateVideo() {
        val newValues = formManager.updatedVideoValues
        val video = storage.videos.first { it.id == newValues["id"] }
        val previousType = video.videoType
        val previousCustomType = video.customTypeName

        deleteFromTypeList(video.id, previousType, previousCustomType) // delete video from previous type list
        if (newValues["newTitle"].toString().isNotBlank()) {
            video.title = newValues["newTitle"].toString()
        }
        video.videoType = newValues["newType"] as VideoType
        video.customTypeName = newValues["newCustomTypeName"].toString()
        storage.updateStorage()
        assignToTypeList(video) // add video to new type list

        formManager.status = "Video updated!"
//        storage.setContent(videos)
    }

    // mocking
    override fun shuffle(): String {
        val idArray = mutableListOf<String>()
        for (video in storage.getContent()) {
            val id = video.id
            idArray.add(id)
        }
        val randomId = idArray.random()
        return randomId
    }

    override fun shuffleByType(videoType: String): String {
//        val assignedType = assignType(videoType)
        val idArray = mutableListOf<String>()
        val videosList = getVideosByType(videoType)

        for (video in videosList) {
            val id = video.id
            idArray.add(id)
        }
        val randomId = idArray.random()
        return randomId
    }


}
