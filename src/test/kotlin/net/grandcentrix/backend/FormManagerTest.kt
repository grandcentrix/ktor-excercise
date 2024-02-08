package net.grandcentrix.backend

import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.util.*
import junit.framework.TestCase
import net.grandcentrix.backend.models.FormManager
import net.grandcentrix.backend.models.VideoType
import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertIsNot

class FormManagerTest {

    companion object {
        val FormManagerInstance: FormManager = FormManager()
        var actionTitle = "Add a new video:"
        var buttonAction = "/add-video"

        const val videoId = "1YBtzAAChU8"
        const val videoTitle = "Lofi Girl - Christmas"
        const val videoLink = "https://www.youtube.com/watch?v=1YBtzAAChU8"
        val videoType = VideoType.MUSIC.name
        const val videoStatus = "Testing!"
    }

    @Test
    fun testGetActionTitle() {
//        assert
//        assertNotNull()
    }

    @Test
    fun testSetVideoParametersBlank() {
        val formParameters = Parameters.build {
            append("link", videoLink)
            append("title", "") // blank value
            append("videoTypes", videoType)
        }

        assertFails("Video link and title cannot be blank or video link is not supported!") { FormManagerInstance.setVideoParameters(formParameters) }
    }

    @Test
    fun testSetVideoParametersWrongUrl() {
        val formParameters = Parameters.build {
            append("link", "https://www.youtube.com/playlist?list=PL6NdkXsPL07KN01gH2vucrHCEyyNmVEx4") // wrong link value
            append("title", videoTitle)
            append("videoTypes", videoType)
        }

        assertFails("Video link is not supported!") { FormManagerInstance.setVideoParameters(formParameters) }
    }

    @Test
    fun testExceptionGetVideoData() {
        val formParameters = Parameters.build {
            append("link", videoLink)
            append("", videoTitle) // wrong or missing parameter name
            append("videoTypes", videoType)
        }

        assertFailsWith(MissingRequestParameterException::class, "Parameter is incorrect") {
            FormManagerInstance.setVideoParameters(
                formParameters
            )
        }
    }

    @Test
    fun testSetParameters() {
        val formParameters = Parameters.build {
            append("link", videoLink)
            append("title", videoTitle)
            append("videoTypes", videoType)
        }

        assertIsNot<MissingRequestParameterException>(FormManagerInstance.setVideoParameters(formParameters))

        assertFails { FormManagerInstance.setVideoParameters(formParameters) }
    }

    @Test
    fun testGetVideoDataParameters() {
        val formParameters = Parameters.build {
            append("link", VideoManagerTest.videoLink)
            append("title", VideoManagerTest.videoTitle)
            append("videoTypes", VideoType.MUSIC.name)
        }

        val id = formParameters.getOrFail("link").substringAfter("v=").substringBefore("&")
        val link = formParameters.getOrFail("link")
        val title = formParameters.getOrFail("title")
        val videoType = formParameters.getOrFail("videoTypes")
        val testMissingParameter = formParameters.getOrFail("test")

        TestCase.assertEquals(MissingRequestParameterException::class, testMissingParameter)

        TestCase.assertEquals(VideoManagerTest.videoId, id)
        TestCase.assertEquals(VideoManagerTest.videoTitle, title)
        TestCase.assertEquals(VideoManagerTest.videoLink, link)
        TestCase.assertEquals(videoType, VideoType.MUSIC.name)
    }
}