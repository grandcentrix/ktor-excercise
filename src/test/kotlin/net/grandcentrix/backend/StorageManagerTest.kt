package net.grandcentrix.backend

import net.grandcentrix.backend.models.StorageManagerFile
import net.grandcentrix.backend.models.StorageManagerMemory
import net.grandcentrix.backend.models.Video
import org.junit.Test
import java.io.File
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class StorageManagerTest {

    companion object {
        val StorageManagerMemoryInstance: StorageManagerMemory = StorageManagerMemory()
        val StorageManagerFileInstance: StorageManagerFile = StorageManagerFile()
    }

    @Test
    fun testListVideosMemory() {
        assertNotNull(StorageManagerMemoryInstance.listVideos())
    }

    @Test
    fun testReturnTypeListVideosMemory() {
        assertIs<MutableList<Video>>(StorageManagerMemoryInstance.listVideos())
    }

    @Test
    fun testListVideosFile() {
        assertNotNull(StorageManagerFileInstance.listVideos())
    }

    @Test
    fun testReturnTypeListVideosFile() {
        assertIs<MutableList<Video>>(StorageManagerFileInstance.listVideos())
    }

    @Test
    fun testGetFile() {
        assertNotNull(StorageManagerFileInstance.getFile())
    }

    @Test
    fun testReturnTypeGetFile() {
        assertIs<File>(StorageManagerFileInstance.getFile())
    }
}