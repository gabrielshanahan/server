package io.github.gabrielshanahan.server

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import org.slf4j.LoggerFactory

const val PORT_ARG = "--port"
const val LOG_LEVEL_ARG = "--level"
const val DEFAULT_PORT = 8080
const val OPERATION_DURATION_MS = 1000L

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
    val serverSocket = ServerSocket(port)

    val serverScope = CoroutineScope(SupervisorJob())

    logger.info("Server started on port $port")
    while (true) {
        val clientSocket = serverSocket.accept()
        logger.debug("Accepted connection from ${clientSocket.inetAddress}:${clientSocket.port}")
        serverScope.launch {
            logger.debug("Launching")
            handle(clientSocket)
        }
    }
}

private fun OutputStream.write(str: String) = write(str.trimIndent().toByteArray())

private suspend fun handle(clientSocket: Socket) {
    val input = clientSocket.getInputStream().bufferedReader()
    val output = clientSocket.getOutputStream()

    val header = input.readLine() // Discard the request header (up to blank line)
    logger.debug("Request header: $header")
    delay(OPERATION_DURATION_MS) // Simulate some work
    output.write(
        """
        HTTP/1.1 200 Okay
        Server: SimpleServer
        Content-Type: text/html
        
        <html><body>Hello, world!</body></html>
    """)

    output.flush()
    clientSocket.close()
}
