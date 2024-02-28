package net.grandcentrix.backend

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import net.grandcentrix.backend.models.StorageManagerFile.Companion.StorageManagerFileInstance
import net.grandcentrix.backend.models.StorageManagerTypesFile.Companion.StorageManagerTypesFileInstance
import net.grandcentrix.backend.models.Video
import net.grandcentrix.backend.models.VideoManager.Companion.VideoManagerInstance
import net.grandcentrix.backend.models.VideoType
import org.junit.Before
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ApplicationTest {

    companion object {
        private const val FILE_NAME = "src/main/resources/testFile.json"
    }

    @Before
    fun beforeTests() {
        mockkStatic(::saveVideos)
        mockkObject(StorageManagerTypesFileInstance, recordPrivateCalls = true)
        mockkObject(StorageManagerFileInstance, recordPrivateCalls = true)
    }

    @AfterTest
    fun afterTest() {
        unmockkAll()
        File(FILE_NAME).writeText("[]")
    }

    @Test
    fun testStorageSetupTypes() {
        every { StorageManagerTypesFileInstance["getFile"]() } returns File(FILE_NAME)

        storageSetup()

        val expectedTypes = VideoType.entries.map { it.name }.toList()
        val actualTypes = StorageManagerTypesFileInstance.getContent()

        assertNotNull(actualTypes)
        assertEquals(expectedTypes, actualTypes)
    }

    @Test
    fun testStorageSetupVideosEmptyFile() {
        every { saveVideos() } returns true
        every { StorageManagerFileInstance["getFile"]() } returns File(FILE_NAME)

        storageSetup()

        val actualVideos = StorageManagerFileInstance.getContent()

        assertNotNull(actualVideos)
        assertEquals(emptyList(), actualVideos)
    }

    @Test
    fun testStorageSetupVideosFile() {
        every { saveVideos() } returns true
        every { StorageManagerFileInstance["getFile"]() } returns File(FILE_NAME)

        val videos = listOf(
            Video("1YBtzAAChU8", "Test", "https://www.youtube.com/watch?v=1YBtzAAChU8", VideoType.NEWS, ""),
            Video("1bvbsx-hpFc", "Test 1", "1bvbsx-hpFc", VideoType.MUSIC, "")
        )

        val videosJson = Json.encodeToJsonElement(videos).toString()
        File(FILE_NAME).writeText(videosJson)

        storageSetup()

        val storageVideos = StorageManagerFileInstance.getContent()
        val videosList = VideoManagerInstance.getVideos()
        val videosByType = VideoManagerInstance.getVideosByType(VideoType.MUSIC.name)

        assertNotNull(storageVideos)
        assertNotNull(videosList)
        assertNotNull(videosByType)

        assertEquals(videos.size, storageVideos.size)
        assertEquals(storageVideos.size, videosList.size)
        assertEquals(1, videosByType.size)

        for (i in videos.withIndex()) {
            assertEquals(videos[i.index].id, storageVideos[i.index].id)
            assertEquals(videos[i.index].title, storageVideos[i.index].title)
            assertEquals(videos[i.index].link, storageVideos[i.index].link)
            assertEquals(videos[i.index].videoType, storageVideos[i.index].videoType)
            assertEquals(videos[i.index].customTypeName, storageVideos[i.index].customTypeName)

            assertEquals(storageVideos[i.index].id, videosList[i.index].id)

        }
    }
}