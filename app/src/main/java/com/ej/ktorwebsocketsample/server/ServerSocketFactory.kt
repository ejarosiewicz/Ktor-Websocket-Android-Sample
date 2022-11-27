package com.ej.ktorwebsocketsample.server

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.aSocket
import kotlinx.coroutines.Dispatchers

class ServerSocketFactory {

    fun create(hostname: String): ServerSocket{
        return aSocket(SelectorManager(Dispatchers.IO)).tcp().bind(hostname, 9002)
    }
}