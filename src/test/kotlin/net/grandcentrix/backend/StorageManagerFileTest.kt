package net.grandcentrix.backend

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import net.grandcentrix.backend.StorageManagerFile.Companion.StorageManagerFileInstance
import net.grandcentrix.backend.enums.VideoType
import net.grandcentrix.backend.models.Video
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.*

class StorageManagerFileTest {

    companion object {
        val video = Video("12345", "Test Video", "", VideoType.MUSIC, "")
        private const val FILE_NAME = "src/main/resources/testFile.json"
    }

    @Before
    fun beforeTests() {
        val video = Video(
            "1YBtzAAChU8",
            "Test",
            "https://www.youtube.com/watch?v=1YBtzAAChU8",
            VideoType.MUSIC,
            ""
        )
        val videosJson = Json.encodeToJsonElement(video).toString()

        File(FILE_NAME).writeText(videosJson)
        mockkObject(StorageManagerFileInstance, recordPrivateCalls = true)
        every { StorageManagerFileInstance["getFile"]() } returns File(FILE_NAME)
    }

    @AfterTest
    fun afterTest() {
        unmockkAll()
        File(FILE_NAME).writeText("[]")
    }

    @Test
    fun testGetContent() {
//        every { StorageManagerFileInstance.getFile() } returns File(FILE_NAME)

        val videoID = "IuTDuvYr7f0" // must exist in the videosList.json file
        val videos = StorageManagerFileInstance.videos
        val video = videos.find { it.id == videoID }!!

        assertNotNull(video)
        assertTrue { videos.size >= 1 }
    }

    @Test
    fun testSetContent() {
//        every { StorageManagerFileInstance.getFile() } returns File(FILE_NAME)
        val videos = mutableListOf(video)
        StorageManagerFileInstance.setContent(videos)
        val id = StorageManagerFileInstance.getContent().find { it.id == video.id }?.id
        assertEquals(video.id, id)
    }

    @Test
    fun testSetItem() {
//        every { StorageManagerFileInstance.getFile() } returns File(FILE_NAME)
        StorageManagerFileInstance.setItem(video)
        val id = StorageManagerFileInstance.getContent().find { it.id == video.id }?.id
        // check if contains added video
        assertEquals(video.id, id)
    }

    @Test
    fun testRemoveItem() {
//        every { StorageManagerFileInstance.getFile() } returns File(FILE_NAME)
        StorageManagerFileInstance.removeItem(video)
        assertFails { assertContains(StorageManagerFileInstance.getContent(), video) }
    }

    @Test
    fun testUpdateStorage() {
//        every { StorageManagerFileInstance.getFile() } returns File(FILE_NAME)
        StorageManagerFileInstance.videos.add(video)
        StorageManagerFileInstance.updateStorage()
        val id = StorageManagerFileInstance.getContent().find { it.id == video.id }?.id
        assertEquals(video.id, id)
    }
}