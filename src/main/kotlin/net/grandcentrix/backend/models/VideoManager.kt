package net.grandcentrix.backend.models;

import net.grandcentrix.backend.models.MusicVideo.Companion.musicVideos
import net.grandcentrix.backend.models.NewsVideo.Companion.newsVideos
import net.grandcentrix.backend.models.StorageManagerMemory.Companion.StorageManagerMemoryInstance

open class VideoManager(var storage: StorageManagerInterface, private val formManager: FormManager) : VideoManagerInterface {
    companion object {
        val VideoManagerInstance: VideoManager = VideoManager(StorageManagerMemoryInstance, FormManager())
    }

    private var videos = storage.listVideos()
    private var link = String()

    override fun defineStorage(storageType: StorageManagerInterface) {
        storage = storageType
    }

    override fun getVideos(): MutableList<Video> {
        return videos
    }

    override fun loadVideosToType(videos: MutableList<Video>) {
        musicVideos = videos.filter { it.videoType == VideoType.MUSIC }.toMutableList()
        newsVideos = videos.filter { it.videoType == VideoType.NEWS }.toMutableList()
    }

    override fun getVideosByType(videoType: String): MutableList<Video> {
        val assignedType = VideoType.assignType(videoType)
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

    override fun findVideo(id: String): Video? {
        val video = videos.find { it.id == id }
        return video
    }

    override fun addVideo() {
        val video = formManager.getVideo()
        if (findVideo(video.id) == null) {
            if (video.videoType == VideoType.MUSIC) {
                val newVideo = MusicVideo(video.id, video.title, link, VideoType.MUSIC)
                    musicVideos.add(newVideo)
                    videos = videos.union(musicVideos).toMutableList()
            } else {
                val newVideo = NewsVideo(video.id, video.title, link, VideoType.NEWS)
                    newsVideos.add(newVideo)
                    videos = videos.union(newsVideos).toMutableList()
            }
            // add video types list to videos
            storage.setVideos(videos)
            formManager.setStatus("Video added!")
        } else {
            formManager.setStatus("Video already exists!")
        }
    }

    override fun deleteVideo(id: String) {
        if (videos.size > 1) {
            val video = findVideo(id)
            videos.remove(video)
            if (findVideo(id) == null) {
                formManager.setStatus("Video deleted!")
            } else {
                formManager.setStatus("Oops, there are some problem while deleting!")
            }
            storage.setVideos(videos)
        } else {
            formManager.setStatus("The list cannot be empty!")
        }
    }

    override fun updateVideo() {
        val newValues = formManager.getUpdatedVideoValues()
        val newVideoType = VideoType.assignType(newValues["newType"].toString())
        val video = findVideo(newValues["id"].toString())

        if (video != null) {
            video.title = newValues["newTitle"].toString()
            video.videoType = newVideoType
            formManager.setStatus("Video updated!")
            storage.setVideos(videos)
        } else {
            formManager.setStatus("Video not found!")
        }
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
        val assignedType = VideoType.assignType(videoType)
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
