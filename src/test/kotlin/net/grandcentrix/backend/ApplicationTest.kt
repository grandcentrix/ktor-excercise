package net.grandcentrix.backend

import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.testing.*
import io.mockk.*
import net.grandcentrix.backend.models.StorageManagerTypesFile.Companion.StorageManagerTypesFileInstance
import net.grandcentrix.backend.models.VideoManager.Companion.VideoManagerInstance
import net.grandcentrix.backend.models.VideoType
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertNotNull

class ApplicationTest {

    private lateinit var server: NettyApplicationEngine

    @Before
    fun beforeTests() {
        mockkStatic(::saveVideos)
        server = embeddedServer(
            Netty, port = 8080, host = "localhost", module = Application::module
        ).start(wait = true)
        mockkStatic(::server)
    }

    @Test
    fun testMain() = testApplication {
        environment {
            config = MapApplicationConfig("ktor.environment" to "test")
        }

        application {
            every { saveVideos() } returns true
            coEvery { server } just Awaits

            main()

            verify(exactly = 0) { server }

            val typeNames = StorageManagerTypesFileInstance.getContent()
            val videos = VideoManagerInstance.getVideos()
            val videosByType = VideoManagerInstance.getVideosByType(VideoType.MUSIC.name)

            // assert that content is equal to the file content
            assertNotNull(typeNames)
            assertNotNull(videos)
            assertNotNull(videosByType)

            assertContains(typeNames, VideoType.MUSIC.name)
        }
    }
}