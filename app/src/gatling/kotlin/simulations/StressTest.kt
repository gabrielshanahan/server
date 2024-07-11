package simulations

import io.gatling.javaapi.core.*
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*
import io.github.gabrielshanahan.server.DEFAULT_PORT
import java.time.Duration

class StressTest : Simulation() {

    val httpProtocol =
        http.baseUrl("http://localhost:$DEFAULT_PORT").acceptHeader("application/json")

    val scn = scenario("StressTest").exec(http("request1").get("/"))

    /**
     * With these numbers, you'll probably start hitting the open files limit on your OS,
     * which manifests as a 'java.net.SocketException: Too many open files' exception.
     *
     * Consult e.g. this on how to fix this (macOS): https://stackoverflow.com/questions/33836092/too-many-open-files-when-executing-gatling-on-mac
     */
    init {
        setUp(
                scn.injectOpen(
                    constantUsersPerSec(5_000.0).during(Duration.ofSeconds(20))))
            .protocols(httpProtocol)
//            .maxDuration(Duration.ofSeconds(10))
    }
}
