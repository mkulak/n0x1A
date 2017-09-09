package com.github.mkulak.n0x1A

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vertx.core.Vertx
import io.vertx.core.json.Json

fun main(args: Array<String>) {
    Json.mapper.registerModule(KotlinModule())

    val statsManager = FastStatsManager(SystemClock(), 60000)

    val port = args.firstOrNull()?.toIntOrNull() ?: 8080
    val api = HttpApi(port, statsManager)

    val vertx = Vertx.vertx()
    vertx.deployVerticle(api) { if (it.failed()) vertx.close() }
}