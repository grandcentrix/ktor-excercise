package net.grandcentrix.backend.models

enum class VideoType() {
    MUSIC, NEWS, GAME, EDU, DOC, LIVE, CUSTOM;
}

fun assignType(videoType: String): VideoType {
    return when (videoType) {
        "MUSIC" -> VideoType.MUSIC
        "NEWS" -> VideoType.NEWS
        "GAME" -> VideoType.GAME
        "EDU" -> VideoType.EDU
        "DOC" -> VideoType.DOC
        "LIVE" -> VideoType.LIVE
        "CUSTOM" -> VideoType.CUSTOM
        else -> {
            VideoType.CUSTOM
        }
    }
}