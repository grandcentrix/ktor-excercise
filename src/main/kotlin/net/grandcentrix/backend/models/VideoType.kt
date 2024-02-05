package net.grandcentrix.backend.models

enum class VideoType {
    MUSIC, NEWS, GAME, EDU, DOC, LIVE, NONEXISTENT;

    companion object {
        fun assignType(videoType: String): VideoType {
            when (videoType) {
                "MUSIC" -> return MUSIC
                "NEWS" -> return NEWS
                else -> {
                    print("Error")
                    return NONEXISTENT
                }
            }
        }
    }
}