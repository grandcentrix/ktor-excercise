package net.grandcentrix.backend.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import net.grandcentrix.backend.StorageManagerTypesFile.Companion.StorageManagerTypesFileInstance

fun Application.configureStatusPages() {
    routing {
        staticResources("/static", "static")
    }

    install(StatusPages) {
        exception<Exception> { call, cause ->
            when (cause) {
                is AuthorizationException -> call.respondTemplate(
                    "error.ftl",
                    mapOf("errorMessage" to "Error 403. Forbidden.")
                )
                is MissingRequestParameterException -> {
                    StorageManagerTypesFileInstance.getContent().forEach {
                         if (call.url().contains(it)) {
                            val path = call.url().substringBefore(it)+"$it/videos"
                            call.respondRedirect(path)
                        }
                    }

                    if (call.url().contains("add-video")) {
                        call.respondRedirect(call.url().substringBeforeLast(call.request.local.uri))
                    }

                    call.respondRedirect(call.url().substringBefore(call.request.local.uri))
                }

                is NoSuchElementException -> {
                    val path = call.url().substringBefore(call.request.local.uri)
                    call.respondRedirect(path)
                }

                else -> call.respondTemplate(
                    "error.ftl",
                    mapOf("errorMessage" to "500: Server error - $cause")
                )
            }
        }

        status(HttpStatusCode.Unauthorized) { call, _ ->
            call.respondTemplate(
                "error.ftl",
                mapOf("errorMessage" to "Error 401. Not authorized."))
        }

        status(HttpStatusCode.NotFound) { call, _ ->
            call.respondTemplate(
                "error.ftl",
                mapOf("errorMessage" to "Oops! It wasn't possible to find the page, or it doesn't exist."))
        }
    }
}

class AuthorizationException(override val message: String?) : Exception()