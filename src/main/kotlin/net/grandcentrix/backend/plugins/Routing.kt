package net.grandcentrix.backend.plugins

import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.grandcentrix.backend.models.videos

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondRedirect("")
        }
        route("/") {
            get {
//                call.respond(FreeMarkerContent("index.ftl", mapOf("videos" to videos)))
            }
        }
    }
}