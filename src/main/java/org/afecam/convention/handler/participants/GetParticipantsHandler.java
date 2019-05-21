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
import io.vertx.core.json.JsonArray;

import java.net.HttpURLConnection;

public class GetParticipantsHandler implements Handler<RoutingContext> {
    private final static Logger LOGGER = LoggerFactory.getLogger(GetParticipantHandler.class);
    private MongoDAO mongoDAO;

    public GetParticipantsHandler(MongoClient dbclient){
        this.mongoDAO = new MongoDAO(dbclient);
    }

    @Override
    public void handle(RoutingContext routingContext) {
        LOGGER.debug("get a batch of "+ Collections.Participant+" {}",
                routingContext.request()
                        .absoluteURI());

        JsonObject query = routingContext.getBodyAsJson();
        Future<JsonArray> future = mongoDAO.search(Collections.Participant, query);

        JsonObject response = new JsonObject();
        routingContext.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON);

        future.setHandler(result -> {
            if(future.succeeded()){
                response.put("success", Collections.Participant + "s Retrieved");
                response.put("dto", future.result());
                routingContext.response().setStatusCode(HttpURLConnection.HTTP_OK);
            }else{
                response.put("error", Collections.Participant + "s Not Found");
                routingContext.response().setStatusCode(HttpURLConnection.HTTP_NOT_FOUND);
            }

            routingContext.response().end(response.encode());
        });
    }
}