package com.github.mkulak.n0x1A

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vertx.core.Vertx
import io.vertx.core.json.Json

fun main(args: Array<String>) {
    Json.mapper.registerModule(KotlinModule())

    val statsManager = StatsManagerImpl(SystemClock(), 60000)
    val api = HttpApi(statsManager)

    val vertx = Vertx.vertx()
    vertx.deployVerticle(api) { if (it.failed()) vertx.close() }
}