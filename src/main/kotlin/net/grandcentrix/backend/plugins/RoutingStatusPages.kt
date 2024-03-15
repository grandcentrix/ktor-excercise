package net.grandcentrix.backend.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.plugins.statuspages.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Exception> { call, cause ->
            when (cause) {
                is AuthorizationException -> call.respondTemplate(
                    "error.ftl",
                    mapOf("errorMessage" to "Error 403. Access not allowed.")
                )
                else -> call.respondTemplate(
                    "error.ftl",
                    mapOf("errorMessage" to "Error 500. Server error.")
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