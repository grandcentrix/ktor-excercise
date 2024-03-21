package net.grandcentrix.backend

import io.ktor.http.*
import io.ktor.server.plugins.*
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import net.grandcentrix.backend.models.FormActionType
import net.grandcentrix.backend.models.FormManager.Companion.FormManagerInstance
import net.grandcentrix.backend.models.StorageManagerTypesFile.Companion.StorageManagerTypesFileInstance
import net.grandcentrix.backend.models.Video
import net.grandcentrix.backend.models.VideoType
import org.junit.Before
import java.io.File
import kotlin.test.*

class FormManagerTest {

    companion object {
        const val VIDEO_ID = "1YBtzAAChU8"
        const val VIDEO_TITLE = "Lo-fi Girl - Christmas"
        const val VIDEO_LINK = "https://www.youtube.com/watch?v=1YBtzAAChU8"
        const val CUSTOM_TYPE_NAME = "TEST"
        val VIDEO_TYPE = VideoType.CUSTOM
        val video = Video(
            VIDEO_ID,
            VIDEO_TITLE,
            VIDEO_LINK,
            VIDEO_TYPE,
            CUSTOM_TYPE_NAME
        )

        val typeNames = VideoType.entries.map { it.name }.toMutableList()
        private const val FILE_NAME = "src/main/resources/testFile.json"
    }

    @Before
    fun beforeTests() {
        val typeNamesJson = Json.encodeToJsonElement(typeNames).toString()
        File(FILE_NAME).writeText(typeNamesJson)
        mockkObject(StorageManagerTypesFileInstance, recordPrivateCalls = true)
        every { StorageManagerTypesFileInstance["getFile"]() } returns File(FILE_NAME)
    }

    @AfterTest
    fun afterTest() {
        unmockkAll()
        File(FILE_NAME).writeText("[]")
    }

    @Test
    fun testSetVideoParameters() {
        val formParameters = Parameters.build {
            append("link", VIDEO_LINK)
            append("title", VIDEO_TITLE)
            append("videoTypes", VIDEO_TYPE.name)
            append("customType", CUSTOM_TYPE_NAME)
        }

        // check if parameter key is missing
        assertIsNot<MissingRequestParameterException>(FormManagerInstance.setVideoParameters(formParameters))
        // check if parameters values are correct

        // check if custom type name was added to file
        FormManagerInstance.setVideoParameters(formParameters)
        assertContains(FormManagerInstance.videoTypes, CUSTOM_TYPE_NAME)
        assertContains(StorageManagerTypesFileInstance.getContent(), CUSTOM_TYPE_NAME)

        //check if video parameters are correct
        assertEquals(video.link, FormManagerInstance.video.link)
        assertEquals(video.title, FormManagerInstance.video.title)
        assertEquals(video.videoType, FormManagerInstance.video.videoType)
        assertEquals(video.customTypeName, FormManagerInstance.video.customTypeName)
    }

    @Test
    fun testSetVideoParametersCustomType() {
        val formParameters = Parameters.build {
            append("link", VIDEO_LINK)
            append("title", VIDEO_TITLE)
            append("videoTypes", "HOLIDAYS")
            append("customType", "")
        }

        val video = Video(
            VIDEO_ID,
            VIDEO_TITLE,
            VIDEO_LINK,
            VIDEO_TYPE,
            "HOLIDAYS"
        )

        FormManagerInstance.setVideoParameters(formParameters)

        // check if a custom type video added to an existing custom type is assigned correctly
        assertEquals(video.videoType, FormManagerInstance.video.videoType)
        assertEquals(video.customTypeName, FormManagerInstance.video.customTypeName)
    }

    @Test
    fun testSetVideoParametersSaveVideoTypesFails() {
        val formParameters = Parameters.build {
            append("link", VIDEO_LINK)
            append("title", VIDEO_TITLE)
            append("videoTypes", "HOLIDAYS")
            append("customType", "SOME TYPE")
        }

        val video = Video(
            VIDEO_ID,
            VIDEO_TITLE,
            VIDEO_LINK,
            VIDEO_TYPE,
            "HOLIDAYS"
        )

        FormManagerInstance.setVideoParameters(formParameters)

        // check if custom type name is correct //
        assertEquals(video.customTypeName, FormManagerInstance.video.customTypeName)
        // test if type name is added to file when it shouldn't
        assertFalse {
            FormManagerInstance.videoTypes.contains(formParameters["customType"])
        }
    }

    @Test
    fun testSetVideoParametersDuplicatedCustomTypeName() {
        val formParameters = Parameters.build {
            append("link", VIDEO_LINK)
            append("title", "Test")
            append("videoTypes", VideoType.CUSTOM.name)
            append("customType", "MUSIC")
        }
        // music type already exists, it should return the video with music type instead of custom

        FormManagerInstance.setVideoParameters(formParameters)

        assertEquals(
            "",
            FormManagerInstance.video.customTypeName
        )

        assertEquals(
            "MUSIC",
            FormManagerInstance.video.videoType.toString()
        )
    }

    @Test
    fun testSetVideoParametersBlank() {
        val formParameters = Parameters.build {
            append("link", VIDEO_LINK)
            append("title", "") // blank value
            append("videoTypes", VideoType.MUSIC.name)
            append("customType", "")
        }

        assertFailsWith(MissingRequestParameterException::class, "") {
            FormManagerInstance.setVideoParameters(formParameters)
        }
    }

    @Test
    fun testSetVideoParametersWrongUrl() {
        val formParameters = Parameters.build {
            append("link",
                "https://www.youtube.com/playlist?list=PL6NdkXsPL07KN01gH2vucrHCEyyNmVEx4") // wrong link value
            append("title", VIDEO_TITLE)
            append("videoTypes", VideoType.MUSIC.name)
            append("customType", "")
        }

        assertFailsWith(MissingRequestParameterException::class, "Parameter is incorrect") {
            FormManagerInstance.setVideoParameters(
                formParameters
            )
        }
    }

    @Test
    fun testSetVideoParametersMissingParameterException() {
        val formParameters = Parameters.build {
            append("link", VIDEO_LINK)
            append("", VIDEO_TITLE) // wrong or missing parameter name
            append("videoTypes", VideoType.MUSIC.name)
            append("customType", "")
        }

        assertFailsWith(MissingRequestParameterException::class, "Parameter is incorrect") {
            FormManagerInstance.setVideoParameters(
                formParameters
            )
        }
    }

    @Test
    fun testSetUpdatedVideoParameters() {
        val formParameters = Parameters.build {
            append("title", "New Title")
            append("videoTypes", VideoType.MUSIC.name)
            append("customType", "")
        }

        FormManagerInstance.setUpdatedVideoParameters(VIDEO_ID, formParameters)

        // check if values are updated correctly
        assertEquals(formParameters["title"], FormManagerInstance.updatedVideoValues["newTitle"])
        assertEquals(formParameters["videoTypes"], FormManagerInstance.updatedVideoValues["newType"].toString())
        assertEquals(formParameters["customType"], FormManagerInstance.updatedVideoValues["newCustomTypeName"])

    }

    @Test
    fun testSetUpdatedVideoParametersCustomTypeBlank() {
        val formParameters = Parameters.build {
            append("title", "New Title")
            append("videoTypes", VideoType.CUSTOM.name)
            append("customType", "")
        }

        assertFailsWith(MissingRequestParameterException::class, "") {
            FormManagerInstance.setUpdatedVideoParameters(VIDEO_ID, formParameters)
        }

        assertEquals("Custom type name cannot be blank!", FormManagerInstance.status)
        assertNotEquals(formParameters["videoTypes"], FormManagerInstance.updatedVideoValues["newType"])
        assertNotEquals(formParameters["customType"], FormManagerInstance.updatedVideoValues["newCustomTypeName"])
    }

    @Test
    fun testSetUpdatedVideoParametersNewCustomType() {
        val formParameters = Parameters.build {
            append("title", "New Title")
            append("videoTypes", VideoType.CUSTOM.name)
            append("customType", "TESTS")
        }

        FormManagerInstance.setUpdatedVideoParameters(VIDEO_ID, formParameters)

        // check if the new custom type name was saved
        assertContains(FormManagerInstance.videoTypes, formParameters["customType"])
        assertContains(StorageManagerTypesFileInstance.getContent(), formParameters["customType"])
        // check if updated values has new custom type name
        assertEquals(formParameters["customType"], FormManagerInstance.updatedVideoValues["newCustomTypeName"])
    }

    @Test
    fun testSetUpdatedVideoParametersCustomType() {
        val formParameters = Parameters.build {
            append("title", "New Title")
            append("videoTypes", "HOLIDAYS")
            append("customType", "")
        }

        FormManagerInstance.setUpdatedVideoParameters(VIDEO_ID, formParameters)

        // check if custom type has correct custom type name
        assertEquals(formParameters["videoTypes"], FormManagerInstance.updatedVideoValues["newCustomTypeName"])
    }

    @Test
    fun testSetUpdatedVideoParametersDuplicatedCustomTypeName() {
        val formParameters = Parameters.build {
            append("link", VIDEO_LINK)
            append("title", "Test")
            append("videoTypes", VideoType.CUSTOM.name)
            append("customType", "MUSIC")
        }
        // music type already exists, it should return the video with music type instead of custom

        FormManagerInstance.setUpdatedVideoParameters(VIDEO_ID, formParameters)

        assertEquals(
            "",
            FormManagerInstance.updatedVideoValues["newCustomTypeName"].toString()
        )

        assertEquals(
            "MUSIC",
            FormManagerInstance.updatedVideoValues["newType"].toString()
        )
    }

    @Test
    fun testUpdateForm() {
        val actionTitle = "Update video:"
        val formAction = "/${VIDEO_ID}/update"
        val formActionType = FormActionType.UPDATE.name
        val formAttributes = mapOf(
            "name" to actionTitle,
            "link" to formAction,
            "type" to formActionType
        )

        FormManagerInstance.updateFormAction(VIDEO_ID, video)

        // check if values for form are updated
        assertEquals(formAttributes, FormManagerInstance.formAttributes)
    }
}