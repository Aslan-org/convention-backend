package org.afecam.convention.handler.users;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import org.afecam.convention.dao.MongoDAO;
import org.afecam.convention.data.Collections;
import org.afecam.convention.responses.MediaTypes;

import java.net.HttpURLConnection;


public class PostUserHandler implements Handler<RoutingContext> {
    private final static Logger LOGGER = LoggerFactory.getLogger(PostUserHandler.class);
    private MongoDAO mongoDAO;

    public PostUserHandler(MongoClient dbClient) {
        this.mongoDAO = new MongoDAO(dbClient);
    }

    @Override
    public void handle(RoutingContext routingContext) {
        LOGGER.debug("post a " + Collections.User + " {}",
            routingContext.request()
                        .absoluteURI());

        JsonObject user = routingContext.getBodyAsJson();
        Future<JsonObject> future = mongoDAO.save(Collections.User, user);

        JsonObject response = new JsonObject();
        routingContext.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON);

        future.setHandler(result -> {

            if (future.succeeded()) {
                response.put("success", Collections.User + " Saved");
                response.put("data", future.result());
                routingContext.response().setStatusCode(HttpURLConnection.HTTP_CREATED);
            } else {
                response.put("error", Collections.User + " Not Saved");
                routingContext.response().setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
            }

            routingContext.response().end(response.encode());

        });
    }
}