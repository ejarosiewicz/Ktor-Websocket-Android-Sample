package com.ej.ktorwebsocketsample.server

import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.Socket
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

class KtorServer(
    private val serverSocketFactory: ServerSocketFactory = ServerSocketFactory(),
    private val bufferSize: Int = 4096,
    private val channelReaderFactory: ChannelReaderFactory = KtorChannelReaderFactory(),
    private val channelWriterFactory: ChannelWriterFactory = KtorChannelWriterFactory(),
    private val runOnce: Boolean = false,
) {

    private lateinit var serverSocket: ServerSocket
    private lateinit var clientSocket: Socket
    private lateinit var channelReader: ChannelReader
    private lateinit var channelWriter: ChannelWriter

    suspend fun start(hostname: String, onReceiveListener: (String) -> Unit = {}) {
        coroutineScope {
            serverSocket = serverSocketFactory.create(hostname)
            while (isActive) {
                clientSocket = serverSocket.accept()
                channelReader = channelReaderFactory.create(clientSocket)
                channelWriter = channelWriterFactory.create(clientSocket)

                try {
                    while (isActive) {
                        val readBuffer = ByteArray(bufferSize)
                        channelReader.read(dst = readBuffer)
                        val result = readBuffer.formatToString()
                        sendMessage(result, onReceiveListener)
                        if (runOnce) return@coroutineScope
                    }
                } catch (e: Exception) {
                    clientSocket.close()
                }

                if (runOnce) return@coroutineScope
            }
        }
    }


    private fun CoroutineScope.sendMessage(
        message: String,
        onReceiveListener: (String) -> Unit
    ) {
        if (!isActive) {
            return
        }
        onReceiveListener(message)
    }

    suspend fun sendResponse(message: String) {
        if (::channelWriter.isInitialized && channelWriter.isClosedForWrite().not()) {
            channelWriter.writeFully(message.toByteArray())
        }
    }
}