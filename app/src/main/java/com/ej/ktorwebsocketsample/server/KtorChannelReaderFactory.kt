package com.ej.ktorwebsocketsample.server

import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.openReadChannel

class KtorChannelReaderFactory: ChannelReaderFactory {

    override fun create(clientSocket: Socket): ChannelReader {
        return KtorChannelReader(clientSocket.openReadChannel())
    }
}