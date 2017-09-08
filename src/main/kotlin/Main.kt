import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("Main")

    val port = 8080
    val vertx = Vertx.vertx()
    val router = Router.router(vertx)
    router.post("/transactions").handler { ctx ->
        ctx.response().setStatusCode(201).end()
    }
    router.get("/statistics").handler { ctx ->
        ctx.response().setStatusCode(200).end("hello")
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