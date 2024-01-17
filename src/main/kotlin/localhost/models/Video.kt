package localhost.models

class Video
private constructor(val id: String, var title: String) {
    companion object {
        fun newVideo(id: String, title: String) = Video(id, title)
    }
}

val videos = mutableListOf(Video.newVideo("1YBtzAAChU8", "Lofi Girl - Christmas 2023"))