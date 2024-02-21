package net.grandcentrix.backend

import io.ktor.http.*

interface YouTubeManagerWithValidator : YouTubeManagerInterface {
    fun validateVideoUrl(newVideoUrl: String?): Pair<HttpStatusCode, String>?
}