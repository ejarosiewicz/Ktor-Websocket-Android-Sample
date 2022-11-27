package com.ej.ktorwebsocketsample.server

import io.ktor.network.sockets.Socket

interface ChannelReaderFactory {
    fun create(clientSocket: Socket): ChannelReader
}