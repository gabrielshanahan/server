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

    init {
        setUp(
                scn.injectOpen(
                    constantUsersPerSec(1000.0).during(Duration.ofSeconds(5)).randomized()))
            .protocols(httpProtocol)
            .maxDuration(Duration.ofSeconds(10))
    }
}
