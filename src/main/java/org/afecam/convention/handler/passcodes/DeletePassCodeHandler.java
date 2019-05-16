package org.afecam.convention.handler.passcodes;

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


public class DeletePassCodeHandler implements Handler<RoutingContext> {
    private final static Logger LOGGER = LoggerFactory.getLogger(DeletePassCodeHandler.class);
    private MongoDAO mongoDAO;

    public DeletePassCodeHandler(MongoClient dbclient) {
        this.mongoDAO = new MongoDAO(dbclient);
    }

    @Override
    public void handle(RoutingContext routingContext) {
        LOGGER.debug("mark a " + Collections.PassCode + " as deleted {}",
                routingContext.request()
                        .absoluteURI());

        String _id = routingContext.request().getParam("id");
        JsonObject query = new JsonObject().put("_id", _id);


        JsonObject response = new JsonObject();
        routingContext.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON);


        Future<JsonObject> future = mongoDAO.delete(Collections.PassCode, query);

        future.setHandler(result -> {
            if (future.succeeded()) {
                response.put("success", Collections.PassCode + " Deleted");
                response.put("data", future.result());
                routingContext.response().setStatusCode(HttpURLConnection.HTTP_NO_CONTENT);
            } else {
                response.put("error", Collections.PassCode + " Not Deleted");
                routingContext.response().setStatusCode(HttpURLConnection.HTTP_NOT_FOUND);
            }

            routingContext.response().end(response.encode());
        });
    }
}
