package com.ej.ktorwebsocketsample.server

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.*

class KtorChannelWriter(private val  writeChannel: ByteWriteChannel): ChannelWriter {

    override suspend fun writeFully(dst: ByteArray) {
        writeChannel.writeFully(dst)
    }

    override suspend fun isClosedForWrite(): Boolean =
        writeChannel.isClosedForWrite


}