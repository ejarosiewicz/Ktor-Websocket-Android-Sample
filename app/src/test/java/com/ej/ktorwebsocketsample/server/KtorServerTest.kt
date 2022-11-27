package com.ej.ktorwebsocketsample.server

import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.Socket
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import org.junit.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals

internal class KtorServerTest {

    private val channelReaderFactory: ChannelReaderFactory = mockk()
    private val channelWriterFactory: ChannelWriterFactory = mockk {
        coEvery { create(any()) } returns channelWriter
    }

    private val channelWriter: ChannelWriter = mockk(relaxUnitFun = true)

    private val clientSocket: Socket = mockk(relaxUnitFun = true)

    private val serverSocket: ServerSocket = mockk {
        coEvery { accept() } returns clientSocket
    }

    private val serverSocketFactory: ServerSocketFactory = mockk {
        every { create(any()) } returns serverSocket
    }

    private val ktorServer = KtorServer(
        serverSocketFactory = serverSocketFactory,
        channelReaderFactory = channelReaderFactory,
        channelWriterFactory = channelWriterFactory,
        runOnce = true
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `verify test message has been received`() = runTest {
        every { channelReaderFactory.create(any()) } returns TestChannelReader("Test message")

        ktorServer.start("aaaa") {
            assertEquals("Test message", it)
        }
    }

    @Test
    fun `verify methods invoked`() = runTest {
        every { channelReaderFactory.create(any()) } returns TestChannelReader("Test message")

        ktorServer.start("aaaa")

        verify { serverSocketFactory.create("aaaa") }
        verify { channelReaderFactory.create(clientSocket) }
        verify { channelWriterFactory.create(clientSocket) }

    }

    @Test
    fun `verify write method if is not closed to write`() = runTest {
        every { channelReaderFactory.create(any()) } returns TestChannelReader("Test message")
        every { channelWriterFactory.create(any()) } returns channelWriter
        coEvery { channelWriter.isClosedForWrite() } returns false

        ktorServer.start("aaaa")
        ktorServer.sendResponse("ddd")

        coVerify { channelWriter.writeFully("ddd".toByteArray())}
    }

    @Test
    fun `verify write method not invoked if is closed to write`() = runTest {
        every { channelReaderFactory.create(any()) } returns TestChannelReader("Test message")
        every { channelWriterFactory.create(any()) } returns channelWriter
        coEvery { channelWriter.isClosedForWrite() } returns true

        ktorServer.start("aaaa")
        ktorServer.sendResponse("ddd")

        coVerify(exactly = 0) { channelWriter.writeFully(any())}
    }

    private class TestChannelReader(private val messageToRead: String) : ChannelReader {
        override suspend fun read(dst: ByteArray) {
            messageToRead.toByteArray().forEachIndexed { index, byte ->
                dst[index] = byte
            }
        }
    }
}