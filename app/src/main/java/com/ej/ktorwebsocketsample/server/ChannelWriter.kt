package com.ej.ktorwebsocketsample.server

interface ChannelWriter {
    suspend fun writeFully(dst: ByteArray)
    suspend fun isClosedForWrite(): Boolean
}