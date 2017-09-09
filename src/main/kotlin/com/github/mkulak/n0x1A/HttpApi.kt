package com.github.mkulak.n0x1A

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import org.slf4j.LoggerFactory


class HttpApi(val statsManager: StatsManager) : AbstractVerticle() {
    val logger = LoggerFactory.getLogger(HttpApi::class.java)

    override fun start(future: Future<Void>) {
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())

        router.post("/transactions").handler { ctx ->
            val transaction = Json.decodeValue(ctx.body, Transaction::class.java)
            logger.debug("received: $transaction")
            statsManager.add(transaction)
            ctx.response().setStatusCode(201).end()
        }
        router.get("/statistics").handler { ctx ->
            val stats = statsManager.getStats()
            logger.debug("responded: $stats")
            ctx.response().setStatusCode(200).end(Json.encode(stats))
        }

        val port = config().getInteger("http.port", 8080)
        vertx.createHttpServer().requestHandler(router::accept).listen(port) {
            if (it.succeeded()) {
                logger.info("Server started on $port")
                future.complete()
            } else {
                logger.error("Fail to start server: ${it.cause()}")
                future.fail(it.cause())
            }
        }
    }
}