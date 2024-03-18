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

fun Application.configureStatusPages() {
    routing {
        staticResources("/static", "static")
    }

    install(StatusPages) {
        exception<Exception> { call, cause ->
            when (cause) {
                is AuthorizationException -> call.respondTemplate(
                    "error.ftl",
                    mapOf("errorMessage" to "Error 403. Access not allowed.")
                )

                is IllegalArgumentException -> call.respondRedirect(call.url())
                is NotFoundException -> call.respondRedirect(call.url())

                is NullPointerException -> call.respondTemplate(
                    "error.ftl",
                    mapOf("errorMessage" to "Oops! It wasn't possible to fulfill your request.")
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