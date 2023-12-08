package net.grandcentrix.backend

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.head
import kotlinx.html.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureRouting()
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondHtml {
                head {
                    title { +"Ktor Sample" }
                }
                body {
                    h1 { +"Hello World!" }
                    button {
                        +"Click me!"
                        onClick = "alert('Button clicked!');"
                    }
                }
            }
        }
    }
}
