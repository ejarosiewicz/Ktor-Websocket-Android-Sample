package com.ej.ktorwebsocketsample.server

interface ChannelReader {
    suspend fun read(dst: ByteArray)
}