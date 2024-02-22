package net.grandcentrix.backend

import io.mockk.mockkObject
import io.mockk.unmockkAll
import net.grandcentrix.backend.models.StorageManagerMemory.Companion.StorageManagerMemoryInstance
import net.grandcentrix.backend.models.Video
import net.grandcentrix.backend.models.VideoType
import org.junit.Before
import org.junit.Test
import kotlin.test.*

class StorageManagerMemoryTest {


    companion object {
        val video = Video("12345", "Test Video", "", VideoType.MUSIC, "")
    }

    @Before
    fun beforeTests() {
        mockkObject(StorageManagerMemoryInstance)
    }

    @AfterTest
    fun afterTest() {
        unmockkAll()
    }

    @Test
    fun testGetContent() {
        val videoID = "1YBtzAAChU8"
        val videos = StorageManagerMemoryInstance.getContent()
        val video = videos.find { it.id == videoID }
        assertNotNull(video)
        assertTrue { videos.size >= 1 }
    }

    @Test
    fun testSetItem() {
        StorageManagerMemoryInstance.setItem(video)
        // check if contains added video
        assertContains(StorageManagerMemoryInstance.getContent(), video)
    }

    @Test
    fun testRemoveItem() {
        StorageManagerMemoryInstance.removeItem(video)
        assertFails { assertContains(StorageManagerMemoryInstance.getContent(), video) }
    }

    @Test
    fun testSetContent() {
        val videos = mutableListOf(video)
        StorageManagerMemoryInstance.setContent(videos)
        assertEquals(StorageManagerMemoryInstance.getContent(), videos)
    }
}