package net.grandcentrix.backend

import net.grandcentrix.backend.VideoManagerTest.Companion.video
import net.grandcentrix.backend.models.StorageManagerFile
import net.grandcentrix.backend.models.StorageManagerMemory
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class StorageManagerTest {

    companion object {
        val StorageManagerMemoryInstance: StorageManagerMemory = StorageManagerMemory()
        val StorageManagerFileInstance: StorageManagerFile = StorageManagerFile()
    }

    @Test
    fun testListVideosMemory() {
        val videos = StorageManagerMemoryInstance.getContent()
        val other = videos.find {
            it.id == video.id &&
                    it.title == video.title &&
                    it.link == video.link &&
                    it.videoType == video.videoType
        }?.id

        assertEquals(video.id, other)
        assertNotNull(videos)
    }

    @Test
    fun testListVideosFile() {
        val videos = StorageManagerFileInstance.getContent()
        val other = videos.find {
            it.id == video.id &&
                    it.title == video.title &&
                    it.link == video.link &&
                    it.videoType == video.videoType
        }?.id

        assertEquals(video.id, other)
        assertNotNull(videos)
    }

    @Test
    fun testGetFile() {
        assertNotNull(StorageManagerFileInstance.getFile())
    }
}