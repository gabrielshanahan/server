package io.github.gabrielshanahan.server

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.concurrent.thread
import okhttp3.OkHttpClient
import okhttp3.Request

class ServerKtTest :
    StringSpec({
        lateinit var serverThread: Thread
        val client = OkHttpClient()

        beforeTest {
            serverThread = thread(start = true) { main(arrayOf("--port", "$DEFAULT_PORT")) }
            Thread.sleep(100)
        }

        "test server responds with expected output" {
            val request = Request.Builder().url("http://localhost:$DEFAULT_PORT").build()

            client.newCall(request).execute().use { response ->
                response.toString() shouldBe
                    "Response{protocol=http/1.1, code=200, message=Okay, url=http://localhost:8000/}"
            }
        }

        afterTest { serverThread.interrupt() }
    })
