package net.grandcentrix.backend

import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty


fun main() {
    val persistLinks = false

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = { module(persistLinks) })
        .start(wait = true)
}



