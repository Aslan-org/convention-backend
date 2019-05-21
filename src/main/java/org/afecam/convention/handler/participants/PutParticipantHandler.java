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


public class PutParticipantHandler implements Handler<RoutingContext> {
    private final static Logger LOGGER = LoggerFactory.getLogger(PutParticipantHandler.class);
    private MongoDAO mongoDAO;

    public PutParticipantHandler(MongoClient dbclient) {
        this.mongoDAO = new MongoDAO(dbclient);
    }

    @Override
    public void handle(RoutingContext routingContext) {
        LOGGER.debug("put a " + Collections.Participant + " {}",
                routingContext.request()
                        .absoluteURI());

        String _id = routingContext.request().getParam("id");
        JsonObject query = new JsonObject().put("_id", _id);
        JsonObject replaceJson = routingContext.getBodyAsJson();

        Future<JsonObject> future = mongoDAO.update(Collections.Participant, query, replaceJson);

        JsonObject response = new JsonObject();
        routingContext.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON);

        future.setHandler(result -> {
            if (future.succeeded()) {
                response.put("success", Collections.Participant + " Updated");
                response.put("dto", future.result());
                routingContext.response().setStatusCode(HttpURLConnection.HTTP_ACCEPTED);
            } else {
                response.put("error", Collections.Participant + " Not Updated");
                routingContext.response().setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
            }

            routingContext.response().end(response.encode());
        });
    }
}