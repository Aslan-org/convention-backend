package org.afecam.convention.services;

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
import java.util.Random;


public class LoginService implements Handler<RoutingContext> {
    private final static Logger LOGGER = LoggerFactory.getLogger(LoginService.class);
    private MongoDAO mongoDAO;

    public LoginService(MongoClient dbclient) {
        this.mongoDAO = new MongoDAO(dbclient);
    }

    @Override
    public void handle(RoutingContext routingContext) {
        LOGGER.debug("Login a " + Collections.User + " {}",
                routingContext.request()
                        .absoluteURI());

        JsonObject response = new JsonObject();
        routingContext.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON);

        JsonObject principal = routingContext.getBodyAsJson();
        JsonObject query = new JsonObject();

        if (principal.getString("method").equals("sms")) {
            query.put("phoneNumber", principal.getString("phoneNumber"));
        } else if (principal.getString("method").equals("email")) {
            query.put("emailAddress", principal.getString("emailAddress"));
        } else {
            response.put("error", "Invalid Login Credentials");
            routingContext.response().setStatusCode(HttpURLConnection.HTTP_FORBIDDEN);
            routingContext.response().end(response.encode());
        }

        Future<JsonObject> future = mongoDAO.retrieveOne(Collections.User, query);

        future.setHandler(result -> {
            if (future.succeeded() && !future.result().isEmpty()) {
                String passCode = PassCodeGenerator.generate(4);
                JsonObject user = future.result();

                JsonObject authClaim = new JsonObject()
                        .put("passCode", passCode)
                        .put("phoneNumber", user.getString("phoneNumber"))
                        .put("emailAddress", user.getString("emailAddress"))
                        .put("role", user.getString("role"))
                        .put("redirect", user.getString("redirect")) //address where user will be redirected after login
                        .put("status", "unclaimed"); // unclaimed, claimed

                mongoDAO.save(Collections.PassCode, authClaim);

                JsonObject notification = new JsonObject()
                        .put("type", principal.getString("method"))
                        .put("title", "Login")
                        .put("status", "new") // new, sent, failed, archived
                        .put("phoneNumber", user.getString("phoneNumber"))
                        .put("emailAddress", user.getString("emailAddress"))
                        .put("body", "Dear " + user.getString("name") + ",\nYour afecam.org Login Pass Code is: " + passCode);

                mongoDAO.save(Collections.Notification, notification);

                response.put("success", "Awaiting pass code validation");
                routingContext.response().setStatusCode(HttpURLConnection.HTTP_MOVED_TEMP);
                routingContext.response().headers().add("Location", "https://admin.afecam.org/passcode");

            } else {
                response.put("error", Collections.User + " Not Found");
                routingContext.response().setStatusCode(HttpURLConnection.HTTP_NOT_FOUND);routingContext.response().end(response.encode());
            }

            routingContext.response().end(response.encode());
        });

    }
}