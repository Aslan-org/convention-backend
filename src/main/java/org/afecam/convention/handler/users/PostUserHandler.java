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
import org.afecam.convention.dto.Collections;
import org.afecam.convention.responses.MediaTypes;
import org.afecam.convention.services.PassCodeGenerator;

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
                String passCode = PassCodeGenerator.generate(4);

                JsonObject authClaim = new JsonObject()
                        .put("passCode", passCode)
                        .put("phoneNumber", user.getString("phoneNumber"))
                        .put("emailAddress", user.getString("emailAddress"))
                        .put("role", user.getString("role"))
                        .put("redirect", user.getString("redirect")) //address where user will be redirected after login
                        .put("status", "unclaimed"); // unclaimed, claimed

                mongoDAO.save(Collections.PassCode, authClaim);

                JsonObject notification = new JsonObject()
                        .put("type", user.getString("method"))
                        .put("title", "Your Login Pass Code")
                        .put("status", "new") // new, sent, failed, archived
                        .put("phoneNumber", user.getString("phoneNumber"))
                        .put("emailAddress", user.getString("emailAddress"))
                        .put("body", "Dear " + user.getString("name") + ",\nYour afecam.org Login Pass Code is: " + passCode);

                mongoDAO.save(Collections.Notification, notification);

                response.put("success", "Awaiting pass code validation");
                routingContext.response().setStatusCode(HttpURLConnection.HTTP_MOVED_TEMP);
                routingContext.response().headers().add("Location", "https://admin.afecam.org/passcode");

            } else {
                response.put("error", Collections.User + " Not Saved");
                routingContext.response().setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
            }

            routingContext.response().end(response.encode());

        });
    }
}