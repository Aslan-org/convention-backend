package org.afecam.convention.handler.participants;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import org.afecam.convention.dao.MongoDAO;
import org.afecam.convention.dto.Collections;
import org.afecam.convention.responses.MediaTypes;

import java.net.HttpURLConnection;


public class PostParticipantHandler implements Handler<RoutingContext> {
    private final static Logger LOGGER = LoggerFactory.getLogger(PostParticipantHandler.class);
    private MongoDAO mongoDAO;

    public PostParticipantHandler(MongoClient dbClient) {
        this.mongoDAO = new MongoDAO(dbClient);
    }

    @Override
    public void handle(RoutingContext routingContext) {
        LOGGER.debug("post a " + Collections.Participant + " {}",
            routingContext.request()
                        .absoluteURI());

        JsonObject user = routingContext.getBodyAsJson();
        Future<JsonObject> future = mongoDAO.save(Collections.Participant, user);

        JsonObject response = new JsonObject();
        routingContext.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON);

        future.setHandler(result -> {

            if (future.succeeded()) {
                response.put("success", Collections.Participant + " Saved");
                response.put("dto", future.result());
                routingContext.response().setStatusCode(HttpURLConnection.HTTP_CREATED);
            } else {
                response.put("error", Collections.Participant + " Not Saved");
                routingContext.response().setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
            }

            routingContext.response().end(response.encode());

        });
    }
}