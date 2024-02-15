package net.grandcentrix.backend.models;

import io.ktor.server.plugins.*
import net.grandcentrix.backend.models.FormManager.Companion.FormManagerInstance
import net.grandcentrix.backend.models.NewsVideo.Companion.newsVideos
import net.grandcentrix.backend.models.StorageManagerMemory.Companion.StorageManagerMemoryInstance

open class VideoManager private constructor(
    private var storage: StorageManagerInterface<MutableList<Video>,MutableList<Video>>,
    private val formManager: FormManager
) : VideoManagerInterface {

    companion object {
        val VideoManagerInstance: VideoManager = VideoManager(StorageManagerMemoryInstance, FormManagerInstance)
        private var musicVideos = mutableListOf<MusicVideo>()
    }

    private var videos: MutableList<Video> = storage.getContent()

    override fun defineStorage(storageType: StorageManagerInterface<MutableList<Video>,MutableList<Video>>) {
        storage = storageType
    }

    override fun getVideos(): MutableList<Video> {
        return videos
    }

    override fun loadVideosToType(videos: MutableList<Video>) {
        musicVideos = videos.filter { it.videoType == VideoType.MUSIC }.map { it as MusicVideo }.toMutableList()
        newsVideos = videos.filter { it.videoType == VideoType.NEWS }.toMutableList()
    }

    override fun getVideosByType(videoType: VideoType): MutableList<out Video> {
        return when (videoType) {
            VideoType.MUSIC -> musicVideos
            VideoType.NEWS -> newsVideos
            else -> mutableListOf()
        }
    }

    private fun deleteFromTypeList(video: Video?, videoType: VideoType) {
        when (videoType) {
            VideoType.MUSIC -> musicVideos.remove(video)
            VideoType.NEWS -> newsVideos.remove(video)
            VideoType.EDU,
            VideoType.DOC,
            VideoType.LIVE,
            VideoType.GAME -> TODO()
            else -> println("Error on deleting video from type list") //FIXME: throw exception?
        }
    }

    override fun findVideo(id: String): Video? = videos.find { it.id == id }

    private fun assignToTypeList(video: Video): Video {
        when (video.videoType) {
            VideoType.MUSIC -> musicVideos.add(video as MusicVideo)
            VideoType.NEWS -> newsVideos.add(video)
            VideoType.EDU,
            VideoType.DOC,
            VideoType.LIVE,
            VideoType.GAME -> TODO()
//            VideoType.CUSTOM -> customVideos.add(video)
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
        videos.add(video)
        assignToTypeList(video)
        storage.setContent(videos)
        formManager.status = "Video added!"
    }

    override fun deleteVideo(id: String) {
        val video = findVideo(id)
        if (!inputIsValid(id)) {
            return
        }

        // delete video from the lists
        deleteFromTypeList(video, video!!.videoType)
        videos.remove(video)

        // check if video is deleted
        // FIXME is this needed? if there was a problem an exception would be raised
    }

    private fun inputIsValid (id: String): Boolean {
        if (videos.size <= 1) {
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
        val video = videos.first { it.id == newValues["id"] }
        val previousType = video.videoType

        deleteFromTypeList(video, previousType) // delete video from previous type list
        video.apply {
            title = newValues["newTitle"].toString()
            videoType = newValues["newType"] as VideoType
            customTypeName = newValues["newCustomTypeName"].toString()
        }
        assignToTypeList(video) // add video to new type list

        formManager.status = "Video updated!"
        storage.setContent(videos)
    }

    // mocking
    override fun shuffle(): String  = videos.map { it.id }.random()

    override fun shuffleByType(videoType: VideoType): String = when (videoType) {
        VideoType.MUSIC -> musicVideos
        VideoType.NEWS -> newsVideos
        //            VideoType.DOC -> videosList = docVideos
        //            VideoType.EDU -> videosList = eduVideos
        //            VideoType.GAME -> videosList = gameVideos
        //            VideoType.LIVE -> videosList = liveVideos
        else -> videos
    }.map {
        it.id
    }.random()
}
