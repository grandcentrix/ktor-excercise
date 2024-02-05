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
    fun testListVideos() {
        assertIs<MutableList<Video>>(StorageManagerMemoryInstance.listVideos())
        assertNotNull(StorageManagerMemoryInstance.listVideos())

        assertIs<MutableList<Video>>(StorageManagerFileInstance.listVideos())
        assertNotNull(StorageManagerFileInstance.listVideos())
    }

    @Test
    fun testGetFile() {
        assertNotNull(StorageManagerFileInstance.getFile())
        assertIs<File>(StorageManagerFileInstance.getFile())
    }

}