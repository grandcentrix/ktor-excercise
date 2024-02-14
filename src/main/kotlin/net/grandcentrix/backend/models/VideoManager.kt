package net.grandcentrix.backend.models;

import net.grandcentrix.backend.models.FormManager.Companion.FormManagerInstance
import net.grandcentrix.backend.models.MusicVideo.Companion.musicVideos
import net.grandcentrix.backend.models.NewsVideo.Companion.newsVideos
import net.grandcentrix.backend.models.StorageManagerMemory.Companion.StorageManagerMemoryInstance

open class VideoManager(var storage: StorageManagerInterface<MutableList<Video>,MutableList<Video>>, val formManager: FormManager) : VideoManagerInterface {
    companion object {
        val VideoManagerInstance: VideoManager = VideoManager(StorageManagerMemoryInstance, FormManagerInstance)
    }

    private var videos: MutableList<Video> = storage.getContent()

    override fun defineStorage(storageType: StorageManagerInterface<MutableList<Video>,MutableList<Video>>) {
        storage = storageType
    }

    override fun getVideos(): MutableList<Video> {
        return videos
    }

    override fun loadVideosToType(videos: MutableList<Video>) {
        musicVideos = videos.filter { it.videoType == VideoType.MUSIC }.toMutableList()
        newsVideos = videos.filter { it.videoType == VideoType.NEWS }.toMutableList()
//        gameVideos = videos.filter { it.videoType == VideoType.GAME }.toMutableList()
//        eduVideos = videos.filter { it.videoType == VideoType.EDU }.toMutableList()
//        docVideos = videos.filter { it.videoType == VideoType.DOC }.toMutableList()
//        liveVideos = videos.filter { it.videoType == VideoType.LIVE }.toMutableList()
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
            else -> mutableListOf()
        }
    }

    private fun deleteFromTypeList(video: Video?, videoType: VideoType) {
        when (videoType) {
            VideoType.MUSIC -> musicVideos.remove(video)
            VideoType.NEWS -> newsVideos.remove(video)
            VideoType.GAME -> TODO()
            VideoType.EDU -> TODO()
            VideoType.DOC -> TODO()
            VideoType.LIVE -> TODO()
            else -> println("Error on deleting video from type list")
        }
    }

    override fun findVideo(id: String): Video? {
        val video = videos.find { it.id == id }
        return video
    }

    private fun assignToTypeList(video: Video): Video {
        when (video.videoType) {
            VideoType.MUSIC -> musicVideos.add(video)
            VideoType.NEWS -> newsVideos.add(video)
            VideoType.GAME -> TODO()
            VideoType.EDU -> TODO()
            VideoType.DOC -> TODO()
            VideoType.LIVE -> TODO()
//            VideoType.CUSTOM -> customVideos.add(video)
            else -> println("Video type not found!")
        }
        return video
    }

    override fun addVideo() {
        val video = formManager.video
        if (findVideo(video.id) == null) {
            videos.add(video)
            assignToTypeList(video)
            storage.setContent(videos)
            formManager.status = "Video added!"
        } else {
            formManager.status = "Video already exists!"
        }
    }

    override fun deleteVideo(id: String) {
        if (videos.size > 1) {
            val video = findVideo(id)

            // delete video from the lists
            if (video !== null) {
                deleteFromTypeList(video, video.videoType)
                videos.remove(video)
            } else {
                formManager.status = "Video not found!"
            }

            // check if video is deleted
            if (findVideo(id) == null) {
                formManager.status = "Video deleted!"
            } else {
                formManager.status = "Oops, there was some problem while deleting!"
            }
            storage.setContent(videos)
        } else {
            formManager.status = "The list cannot be empty!"
        }
    }

    override fun updateVideo() {
        val newValues = formManager.updatedVideoValues
        val video = videos.first { it.id == newValues["id"] }
        val previousType = video.videoType

        deleteFromTypeList(video, previousType) // delete video from previous type list
        video.title = newValues["newTitle"].toString()
        video.videoType = newValues["newType"] as VideoType
        video.customTypeName = newValues["newCustomTypeName"].toString()
        assignToTypeList(video) // add video to new type list

        formManager.status = "Video updated!"
        storage.setContent(videos)
    }

    // mocking
    override fun shuffle(): String {
        val idArray = mutableListOf<String>()
        for (video in videos) {
            val id = video.id
            idArray.add(id)
        }
        val randomId = idArray.random()
        return randomId
    }

    override fun shuffleByType(videoType: String): String {
        val assignedType = assignType(videoType)
        val idArray = mutableListOf<String>()

        val videosList = when (assignedType) {
            VideoType.MUSIC -> musicVideos
            VideoType.NEWS -> newsVideos
    //            VideoType.DOC -> videosList = docVideos
    //            VideoType.EDU -> videosList = eduVideos
    //            VideoType.GAME -> videosList = gameVideos
    //            VideoType.LIVE -> videosList = liveVideos
            else -> videos
        }

        for (video in videosList) {
            val id = video.id
            idArray.add(id)
        }

        val randomId = idArray.random()
        return randomId
    }


}
