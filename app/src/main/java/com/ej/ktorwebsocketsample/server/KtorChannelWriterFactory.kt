package com.ej.ktorwebsocketsample.server

import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel

class KtorChannelWriterFactory: ChannelWriterFactory {

    override fun create(clientSocket: Socket): ChannelWriter{
        return KtorChannelWriter(clientSocket.openWriteChannel(autoFlush = true))
    }
}