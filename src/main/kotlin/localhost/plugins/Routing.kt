package localhost.plugins

import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import localhost.models.videos

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondRedirect("index")
        }
        route("/index") {
            get {
                call.respond(FreeMarkerContent("index.ftl", mapOf("videos" to videos)))
            }
        }
    }
}