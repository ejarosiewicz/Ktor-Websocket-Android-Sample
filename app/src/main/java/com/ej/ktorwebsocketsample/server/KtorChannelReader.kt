package com.ej.ktorwebsocketsample.server

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.*

class KtorChannelReader(private val  readChannel: ByteReadChannel): ChannelReader {

    override suspend fun read(dst: ByteArray) {
        readChannel.readAvailable(dst = dst)
    }
}