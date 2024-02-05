package net.grandcentrix.backend.models;

import io.ktor.http.*
import io.ktor.server.util.*
import net.grandcentrix.backend.models.MusicVideo.Companion.musicVideos
import net.grandcentrix.backend.models.NewsVideo.Companion.newsVideos
import net.grandcentrix.backend.models.StorageManagerMemory.Companion.StorageManagerMemoryInstance

open class VideoManager(storage: StorageManagerInterface) : VideoManagerInterface {
    companion object {
        val VideoManagerInstance: VideoManager = VideoManager(StorageManagerMemoryInstance)
        var link = String()
        var actionTitle = "Add a new video:"
        var buttonAction = "/add-video"
        var youtubeUrls = listOf("https://www.youtube.com/watch?v=", "https://youtube.com/watch?v=", "youtube.com/watch?v=", "www.youtube.com/watch?v=")
    }

    private var videos = storage.listVideos()
    override var storeIn = storage
    override var status = String()

    override fun getVideos(): MutableList<Video> {
        return videos
    }

    override fun getVideosByType(videoType: String): MutableList<Video> {
        val assignedType = VideoType.assignType(videoType)
        return when (assignedType) {
            VideoType.MUSIC -> musicVideos
            VideoType.NEWS -> newsVideos
    //            VideoType.DOC -> return musicVideos
    //            VideoType.EDU -> return musicVideos
    //            VideoType.GAME -> return musicVideos
    //            VideoType.LIVE -> return musicVideos
            else -> mutableListOf()
        }
    }

    override fun findVideo(id: String): Video? {
        val video = videos.find { it.id == id }
        return video
    }

    fun getVideoData(formParameters: Parameters) {
        val id = formParameters.getOrFail("link").substringAfter("v=").substringBefore("&")
        val link = formParameters.getOrFail("link")
        val title = formParameters.getOrFail("title")
        val videoType = formParameters.getOrFail("videoTypes")

        if (id.isBlank() || title.isBlank()) {
            status = "Video link and title cannot be blank or video link is not supported!"
        } else if (!(link.startsWith(youtubeUrls.get(0)) || link.startsWith(youtubeUrls.get(1)) || link.startsWith(youtubeUrls.get(2)) || link.startsWith(youtubeUrls.get(3)))) {
            status = "Video link is not supported!"
        } else {
            addVideo(id, title, link, videoType)
        }
    }

    override fun addVideo(id: String, title: String, link: String, videoType: String) {

        val assignedType = VideoType.assignType(videoType)

        if (findVideo(id) == null) {
            if (assignedType == VideoType.MUSIC) {
                val newVideo = MusicVideo(id, title, link, VideoType.MUSIC)
                    musicVideos.add(newVideo)
                    videos = videos.union(musicVideos).toMutableList()
            } else {
                val newVideo = NewsVideo(id, title, link, VideoType.NEWS)
                    newsVideos.add(newVideo)
                    videos = videos.union(newsVideos).toMutableList()
            }
            // add video types list to videos
            storeIn.updateStorage(videos)
            status = "Video added!"
        } else {
            status = "Video already exists!"
        }
    }

    override fun deleteVideo(id: String) {
        if (videos.size > 1) {
            val video = findVideo(id)
            videos.remove(video)
            status = if (findVideo(id) == null) {
                "Video deleted!"
            } else {
                "Oops, there are some problem while deleting!"
            }
            storeIn.updateStorage(videos)
        } else {
            status = "The list cannot be empty!"
        }
    }

    fun getUpdatedData(id: String, formParameters: Parameters) {
        val newTitle = formParameters.getOrFail("title")
        val newType = formParameters.getOrFail("videoTypes")
        val videoType = VideoType.assignType(newType)

        if (newTitle.isBlank()) {
            status = "Video title cannot be blank!"
        } else {
            updateVideo(id, newTitle, videoType)
            actionTitle = "Add a new video:"
            buttonAction = "/add-video"

        }
    }

    override fun updateVideo(id: String, newTitle: String, newType: VideoType) {
        val video = findVideo(id)
        if (video != null) {
            video.title = newTitle
            video.videoType = newType
            status = "Video updated!"
            storeIn.updateStorage(videos)
        } else {
            status = "Video not found!"
        }
    }

    override fun updateForm(id: String) {
        actionTitle = "Update video title:"
        buttonAction = "$id/update"
        val video = videos.single { it.id == id }
        link = video.link
    }

    override fun shuffle(): String {
        val idArray = mutableListOf<String>()
        for (video in videos) {
            val id = video.id
            idArray.add(id)
        }
        val randomId = idArray.random()
        return randomId
    }
}
