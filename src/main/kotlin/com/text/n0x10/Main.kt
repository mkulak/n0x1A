package com.text.n0x10

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("Main")
    
    Json.mapper.registerModule(KotlinModule())

    val statsManager = StatsManagerImpl(SystemClock(), 60000)

    val port = 8080
    val vertx = Vertx.vertx()
    val router = Router.router(vertx)
    router.route().handler(BodyHandler.create())

    router.post("/transactions").handler { ctx ->
        val transaction = Json.decodeValue(ctx.body, Transaction::class.java)
        println("received: $transaction")
        statsManager.add(transaction)
        ctx.response().setStatusCode(201).end()
    }
    router.get("/statistics").handler { ctx ->
        val stats = statsManager.get()
        ctx.response().setStatusCode(200).end(Json.encode(stats))
    }

    vertx.createHttpServer().requestHandler(router::accept).listen(port) {
        if (it.succeeded()) {
            logger.info("Server started on $port")
        } else {
            logger.error("Fail to start server: ${it.cause()}")
            vertx.close()
        }
    }
}