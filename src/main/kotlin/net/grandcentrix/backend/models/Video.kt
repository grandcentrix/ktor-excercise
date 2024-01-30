package net.grandcentrix.backend.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.io.File

@Serializable
data class Video (var id: String, var title: String, var link: String) {
    interface VideoManagerInterface {
        var status: String

        fun getFile(): File
        fun addVideo(id: String, title: String, link: String)
        fun getVideos(): MutableList<Video>
        fun findVideo(id: String): Video?
        fun deleteVideo(id: String)
        fun updateVideo(id: String, newTitle: String)
        fun updateForm(id: String): String
        fun shuffle(): String
    }

    class VideoManager private constructor() : VideoManagerInterface {
        companion object {
            val VideoManagerInstance: VideoManager = VideoManager()
            const val videoID = "1YBtzAAChU8"
            const val videoTitle = "Lofi Girl - Christmas"
            const val videoLink = "https://www.youtube.com/watch?v=1YBtzAAChU8&list=PL6NdkXsPL07KN01gH2vucrHCEyyNmVEx4&index=2&pp=iAQB8AUB"
            var actionTitle = "Add a new video:"
            var buttonAction = "/add-video"
        }

        private var videos = mutableListOf(Video(videoID, videoTitle, videoLink))
        override var status = String()

        override fun getFile(): File {
            val fileName = "src/main/resources/videosList.json"
            val file = File(fileName)
            return file
        }

        override fun getVideos(): MutableList<Video> {
            return videos

        }

        private fun updateJson() {
            val videosJson = Json.encodeToJsonElement(videos).toString()
            getFile().writeText(videosJson)
        }

        override fun addVideo(id: String, title: String, link: String) {
            if (findVideo(id) == null) {
                val newVideo = Video(id, title, link)
                videos.add(newVideo)
                updateJson()
                status = "Video added!"
            } else {
                status = "Video already exists!"
            }
        }

        override fun findVideo(id: String): Video? {
            val video = videos.find { it.id == id }
            return video
        }

        override fun deleteVideo(id: String) {
            if (videos.size > 1) {
                val video = findVideo(id)
                videos.remove(video)
                updateJson()
                status = if (findVideo(id) == null) {
                    "Video deleted!"
                } else {
                    "Oops, there are some problem while deleting!"
                }
            } else {
                status = "The list cannot be empty!"
            }
        }

        override fun updateVideo(id: String, newTitle: String) {
            val video = findVideo(id)
            if (video != null) {
                video.title = newTitle
                status = "Video updated!"
            } else {
                status = "Video not found!"
            }
        }

        override fun updateForm(id: String): String {
            actionTitle = "Update video title:"
            buttonAction = "$id/update"
            val video = videos.single { it.id == id }
            return video.link
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
}