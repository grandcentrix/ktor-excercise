package net.grandcentrix.backend.enums

enum class VideoType {
    MUSIC, NEWS, CUSTOM;
//    GAME, EDU, DOC, LIVE
}


fun assignType(videoType: String): VideoType {
    return when (videoType) {
        "MUSIC" -> VideoType.MUSIC
        "NEWS" -> VideoType.NEWS
//        "GAME" -> VideoType.GAME
//        "EDU" -> VideoType.EDU
//        "DOC" -> VideoType.DOC
//        "LIVE" -> VideoType.LIVE
        "CUSTOM" -> VideoType.CUSTOM
        else -> {
            VideoType.CUSTOM
        }
    }
}