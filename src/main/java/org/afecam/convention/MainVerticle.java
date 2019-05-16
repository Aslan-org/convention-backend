package org.afecam.convention;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.afecam.convention.data.Collections;
import org.afecam.convention.handler.HealthCheckHandler;
import org.afecam.convention.handler.ResourceNotFoundHandler;
import org.afecam.convention.handler.messages.*;
import org.afecam.convention.handler.notifications.*;

public class MainVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);
    private MongoClient dbClient;

    private Future<Void> prepareDatabase() {
        Future<Void> future = Future.future();
        initDB();
        if (dbClient != null) {
            future.complete();
        }
        return future;
    }

    private Future<Void> startHttpServer() {
        Future<Void> future = Future.future();

        HttpServer server = vertx.createHttpServer();


        LOGGER.debug("in mainVerticle.start(..)");
        Router router = Router.router(vertx);
        router.route()
                .handler(
                        CorsHandler.create("*")
                                .allowedMethod(HttpMethod.GET)
                                .allowedMethod(HttpMethod.POST)
                                .allowedMethod(HttpMethod.PUT)
                                .allowedMethod(HttpMethod.OPTIONS)
                                .allowedHeader(HttpHeaders.ACCEPT.toString())
                                .allowedHeader(HttpHeaders.CONTENT_TYPE.toString()));

        // Decode body of all requests
        router.route().handler(BodyHandler.create());

        // Health Check
        router.get("/health")
                .handler(new HealthCheckHandler());

        // Error
        router.route()
                .last()
                .handler(new ResourceNotFoundHandler());

        // messages endpoint
        router.mountSubRouter("/messages", messagesRoutes());

        // notifications endpoint
        router.mountSubRouter("/notifications", notificationsRoutes());

        server.requestHandler(router::accept)
                .listen(8888, ar -> {
                    if (ar.succeeded()) {
                        LOGGER.info("HTTP server running on port 8888");
                        future.complete();
                    } else {
                        LOGGER.error("Could not start a HTTP server", ar.cause());
                        future.fail(ar.cause());
                    }
                });
        return future;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Future<Void> steps = prepareDatabase().compose(v -> startHttpServer());
        steps.setHandler(startFuture.completer());
    }

    private Router messagesRoutes() {
        LOGGER.debug("Mounting '/messages' endpoint");
        Router router = Router.router(vertx);
        //Get
        router.get("/").handler(new GetMessagesHandler(dbClient));
        router.get("/:id").handler(new GetMessageHandler(dbClient));
        //post
        router.post("/").handler(new PostMessageHandler(dbClient));
        //put
        router.put("/:id").handler(new PutMessageHandler(dbClient));
        //delete
        router.delete("/:id").handler(new DeleteMessageHandler(dbClient));

        return router;
    }

    private Router notificationsRoutes() {
        LOGGER.debug("Mounting '/notifications' endpoint");
    
        Router router = Router.router(vertx);
        //Get
        router.get("/").handler(new GetNotificationsHandler(dbClient));
        router.get("/:id").handler(new GetNotificationHandler(dbClient));
        //post
        router.post("/").handler(new PostNotificationHandler(dbClient));
        //put
        router.put("/:id").handler(new PutNotificationHandler(dbClient));
        //delete
        router.delete("/:id").handler(new DeleteNotificationHandler(dbClient));
    
        return router;
      }

    private void initDB() {
        JsonObject dbConfig = new JsonObject().put("host", "172.17.0.2")
                .put("port", 27017)
                .put("username", "convention-user")
                .put("password", "convention-password")
                .put("db_name", "convention")
                .put("authMechanism", "SCRAM-SHA-1")
                .put("authSource", "admin");

        this.dbClient = MongoClient.createShared(vertx, dbConfig);

        //drop collections
//    for (Collections c: Collections.values()){
//      this.dbClient.dropCollection(c.name(), res -> {
//        if (res.succeeded()) {
//          LOGGER.info("dropped "+c+" collection");
//        } else {
//          LOGGER.debug(res.cause());
//        }
//      });
//    }

        //create collections
        for (Collections c : Collections.values()) {
            this.dbClient.createCollection(c.name(), res -> {
                if (res.succeeded()) {
                    LOGGER.info("Created " + c + " collection");
                } else {
                    LOGGER.debug(res.cause());
                }
            });
        }
    }

}
