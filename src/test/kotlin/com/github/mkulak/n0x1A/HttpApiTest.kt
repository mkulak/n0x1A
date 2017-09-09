package com.github.mkulak.n0x1A

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import io.vertx.ext.web.client.WebClient
import org.junit.*
import org.junit.runner.RunWith
import java.net.ServerSocket


@RunWith(VertxUnitRunner::class)
class HttpApiTest {
    val vertx = Vertx.vertx()
    val client = WebClient.create(vertx)
    val port = getRandomFreePort()
    val statsManager = TestStatsManager(Stats(100.0, 11.0, -1.0, 99.0, 4))

    @Before
    fun setUp(context: TestContext) {
        Json.mapper.registerModule(KotlinModule())
        val options = DeploymentOptions().setConfig(JsonObject().put("http.port", port))
        vertx.deployVerticle(HttpApi(statsManager), options, context.asyncAssertSuccess())
    }

    @After
    fun tearDown(context: TestContext) {
        vertx.close(context.asyncAssertSuccess())
    }

    @Test
    fun `statistics should respond with result from StatsManager`(context: TestContext) {
        val async = context.async()
        client.get(port, "localhost", "/statistics").send {
            if (it.succeeded()) {
                val response = it.result()
                context.assertEquals(200, response.statusCode())
                val received = Json.mapper.readTree(response.body().toString())
                val expected = statsManager.response
                context.assertEquals(expected.sum, received["sum"].asDouble())
                context.assertEquals(expected.avg, received["avg"].asDouble())
                context.assertEquals(expected.min, received["min"].asDouble())
                context.assertEquals(expected.max, received["max"].asDouble())
                context.assertEquals(expected.count, received["count"].asInt())
                async.complete()
            } else context.fail(it.cause())
        }
    }

    @Test
    fun `transactions should respond with 201`(context: TestContext) {
        val async = context.async()
        val amount = 12.0
        val timestamp = 100500L
        val body = mapOf("amount" to amount, "timestamp" to timestamp)
        client.post(port, "localhost", "/transactions").sendJson(body) {
            if (it.succeeded()) {
                val response = it.result()
                context.assertEquals(201, response.statusCode())
                context.assertEquals(Transaction(amount, timestamp), statsManager.received.get())
                async.complete()
            } else context.fail(it.cause())
        }
    }

    private fun getRandomFreePort(): Int {
        val socket = ServerSocket(0)
        val port = socket.localPort
        socket.close()
        return port
    }
}
