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

val youtubeLinks = listOf(
    "https://www.youtube.com/watch?v=aKSxbt-O6TA",
    "https://www.youtube.com/watch?v=7C2z4GqqS5E",
    "https://www.youtube.com/watch?v=eaUpme4jalE",
)

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondHtml {
                head {
                    title { +"Ktor Test" }
                }
                body {
                    h1 { +"Random MV player" }
                    button {
                        +"Click me for a random mv!"
                        script {
                            unsafe {
                                raw("""
                                    document.addEventListener('DOMContentLoaded', function() {
                                        document.querySelector('button').onclick = function(event) {
                                            if (event.target === this) {
                                                alert('Button clicked!');
                                            }
                                        };
                                    });
                                """.trimIndent())
                            }
                        }
                    }
                }
            }
        }
    }
}



