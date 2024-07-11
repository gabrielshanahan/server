package io.github.gabrielshanahan.server

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.OutputStream
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

const val PORT_ARG = "--port"
const val LOG_LEVEL_ARG = "--level"
const val DEFAULT_PORT = 8080
const val OPERATION_DURATION_MS = 1000L
const val BUFFER_SIZE = 1024

private val logger = LoggerFactory.getLogger("TOPLEVEL")

private fun setLogLevel(levelStr: String) {
    val context = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger = context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
    val level = Level.toLevel(levelStr, Level.INFO)
    logger.info("Setting root logger level to $level")
    rootLogger.level = level
}

private typealias CliArguments = Array<String>

private fun CliArguments.valueOf(argument: String): String? {
    val index = indexOf(argument)
    if (index == -1) return null

    return getOrNull(index + 1)
}

private fun CliArguments.stringValueOf(argument: String, default: String) = valueOf(argument) ?: default

private fun CliArguments.intValueOf(argument: String, default: Int) =
    valueOf(argument)?.toIntOrNull() ?: default

fun main(args: CliArguments) {
    setLogLevel(args.stringValueOf(LOG_LEVEL_ARG, Level.INFO.levelStr))
    val port = args.intValueOf(PORT_ARG, DEFAULT_PORT)

    val serverSocket = AsynchronousServerSocketChannel.open().bind(InetSocketAddress(port))
    val serverScope = CoroutineScope(SupervisorJob())

    logger.info("Server started on port $port")
    runBlocking {
        while (true) {
            val socketChannel = serverSocket.awaitConnection()
            if (!socketChannel.isOpen) continue
            logger.debug("Accepted connection from ${socketChannel.remoteAddress}")
            serverScope.launch {
                logger.debug("Launching")
                handle(socketChannel)
            }
        }
    }
}

private suspend fun handle(socketChannel: AsynchronousSocketChannel) = socketChannel.use {
    val request = socketChannel.read()
    logger.debug("Request: $request")
    var response = "<html><body>Hello, world!</body></html>"
//    delay(OPERATION_DURATION_MS) // Simulate some work
    // We need to include the content-length, otherwise cURL will complain with
    // 'no chunk, no close, no size. Assume close to signal end'
    socketChannel.write("""
        HTTP/1.1 200 Okay
        Server: SimpleServer
        Content-Type: text/html
        Content-Length: ${response.length}
        
        $response
    """.trimIndent())
}

private suspend fun AsynchronousServerSocketChannel.awaitConnection(): AsynchronousSocketChannel = suspendCoroutine { cont ->
    accept(null, object : CompletionHandler<AsynchronousSocketChannel, Nothing?> {
        override fun completed(socketChannel: AsynchronousSocketChannel, attachment: Nothing?) = cont.resume(socketChannel)
        override fun failed(exc: Throwable, attachment: Nothing?) = cont.resumeWithException(exc)
    })
}

private suspend fun AsynchronousSocketChannel.read() = buildString {
    while (true) {
        val chunk = suspendCoroutine { cont ->
            val buffer = ByteBuffer.allocate(BUFFER_SIZE)
            read(buffer, null, object : CompletionHandler<Int, Nothing?> {
                override fun completed(bytesRead: Int, attachment: Nothing?) = if (bytesRead > 0) cont.resume(String(buffer.array(), 0, bytesRead)) else cont.resume("")
                override fun failed(exc: Throwable, attachment: Nothing?) = cont.resumeWithException(exc)
            })
        }
        append(chunk)
        if (chunk.length < BUFFER_SIZE) break
    }
}


private suspend fun AsynchronousSocketChannel.write(response: String): Unit = suspendCoroutine { cont ->
    val buffer = ByteBuffer.wrap(response.toByteArray())
    write(buffer, null, object : CompletionHandler<Int, Nothing?> {
        override fun completed(bytesRead: Int, attachment: Nothing?) = cont.resume(Unit)
        override fun failed(exc: Throwable, attachment: Nothing?) = cont.resumeWithException(exc)
    })
}

private fun OutputStream.write(str: String) = write(str.trimIndent().toByteArray())
