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
    "https://www.youtube.com/embed/aKSxbt-O6TA",
    "https://www.youtube.com/embed/7C2z4GqqS5E",
    "https://www.youtube.com/embed/Onq7Njnlug4?si=XH7ltXMRyTQ7EXiH"
)

fun getRandomYouTubeVideoUrl(): String {
    val randomIndex = (0 until youtubeLinks.size).random()
    return youtubeLinks[randomIndex]
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondHtml {
                head {
                    title { +"Ktor Test" }
                }
                body {
                    h1 { +"Random MV player" }
                    iframe {
                        width = "560"
                        height = "315"
                        src = getRandomYouTubeVideoUrl()
                        attributes["allowfullscreen"] = ""
                    }
                    button {
                        +"Click me for a different random MV!"
                        script {
                            unsafe {
                                raw("""
                                    document.addEventListener('DOMContentLoaded', function() {
                                        document.querySelector('button').onclick = function(event) {
                                            if (event.target === this) {
                                                this.onclick = function() {
                                                    var links = ${youtubeLinks.joinToString(prefix = "[", postfix = "]", transform = { "\"$it\"" })};
                                                    var randomIndex = Math.floor(Math.random() * links.length);
                                                    var randomLink = links[randomIndex];
                                                    document.querySelector('iframe').src = randomLink;
                                                }
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




