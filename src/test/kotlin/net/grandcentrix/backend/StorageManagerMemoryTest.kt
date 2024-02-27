package net.grandcentrix.backend

import io.mockk.every
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
        val video1 = Video(
            "-R0UYHS8A_A",
            "Lo-fi Jazz",
            "https://www.youtube.com/watch?v=-R0UYHS8A_A",
            VideoType.MUSIC,
            ""
        )
        val video2 = Video(
            "1bvbsx-hpFc",
            "Lo-fi Summer",
            "https://www.youtube.com/watch?v=1bvbsx-hpFc",
            VideoType.NEWS,
            ""
        )
        val videos = mutableListOf(video1, video2)
    }

    @Before
    fun beforeTests() {
        mockkObject(StorageManagerMemoryInstance)
        every { StorageManagerMemoryInstance.videos } returns videos
    }

    @AfterTest
    fun afterTest() {
        unmockkAll()
    }

    @Test
    fun testGetContent() {
        val videosList = StorageManagerMemoryInstance.getContent()
        val video = videosList.find { it.id == video1.id }
        assertNotNull(video)
        assertTrue { videosList.isNotEmpty() }
    }

    @Test
    fun testSetItem() {
        val newVideo = Video(
            "0MiR7bC9B5o",
            "Test",
            "https://www.youtube.com/watch?v=0MiR7bC9B5o",
            VideoType.CUSTOM,
            "HOLIDAYS"
        )
        StorageManagerMemoryInstance.setItem(newVideo)
        // check if contains added video
        assertContains(StorageManagerMemoryInstance.getContent(), newVideo)
    }

    @Test
    fun testRemoveItem() {
        StorageManagerMemoryInstance.removeItem(video2)
        assertFails { assertContains(StorageManagerMemoryInstance.getContent(), video2) }
    }

    @Test
    fun testSetContent() {
        StorageManagerMemoryInstance.setContent(videos)
        assertEquals(StorageManagerMemoryInstance.getContent(), videos)
    }
}