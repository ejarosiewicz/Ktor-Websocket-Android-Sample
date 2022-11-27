package com.ej.ktorwebsocketsample.server

import io.ktor.network.sockets.Socket

interface ChannelWriterFactory {
    fun create(clientSocket: Socket): ChannelWriter
}