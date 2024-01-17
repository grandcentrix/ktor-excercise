package localhost

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import localhost.plugins.configureRouting
import localhost.plugins.configureSecurity
import localhost.plugins.configureTemplating

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSecurity()
    configureTemplating()
    configureRouting()
}


