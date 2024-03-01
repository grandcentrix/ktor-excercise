package net.grandcentrix.backend

import freemarker.cache.ClassTemplateLoader
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.freemarker.*
import io.ktor.server.testing.*
import net.grandcentrix.backend.plugins.configureStatusPages
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class RoutingStatusPagesTest {

    @Test
    fun testErrorPage() = testApplication {
        application {
            configureStatusPages()
        }

        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }

        val response = client.get("/videos")

        assertContains(response.bodyAsText(), "Oops! It wasn't possible to find the page, or it doesn't exist.")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}