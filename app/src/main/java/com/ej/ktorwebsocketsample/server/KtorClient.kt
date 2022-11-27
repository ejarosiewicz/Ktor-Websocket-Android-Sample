package com.ej.ktorwebsocketsample.server

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.BindException

class KtorClient(
    private val onReceiveListener: (String) -> Unit,
    private val bufferSize: Int = 4096,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) {

    lateinit var serverSocket: ServerSocket
    lateinit var clientSocket: Socket
    lateinit var readChannel: ByteReadChannel
    lateinit var writeChannel: ByteWriteChannel

    suspend fun start(hostname: String) = withContext(dispatcherIO) {
        clientSocket = aSocket(SelectorManager(Dispatchers.IO)).tcp().connect(hostname, 9002)
        readChannel = clientSocket.openReadChannel()
        writeChannel = clientSocket.openWriteChannel(autoFlush = true)

        try {
            while (true) {
                val readBuffer = ByteArray(bufferSize)
                readChannel.readAvailable(dst = readBuffer)
                val result = readBuffer.formatToString()
                sendMessage(result)
            }
        } catch (e: Exception) {
            clientSocket.close()
        }
    }

    private suspend fun sendMessage(message: String) {
        withContext(Dispatchers.Main) {
            onReceiveListener(message)
        }
    }

    suspend fun sendResponse(message: String) = withContext(dispatcherIO) {
        if (writeChannel.isClosedForWrite.not()) {
            writeChannel.writeFully(message.toByteArray())
        }
    }
}