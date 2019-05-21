package org.afecam.convention.services;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.jwt.JWTOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import org.afecam.convention.dao.MongoDAO;
import org.afecam.convention.dto.Collections;
import org.afecam.convention.responses.MediaTypes;

import java.net.HttpURLConnection;


public class ClaimPassCodeService implements Handler<RoutingContext> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ClaimPassCodeService.class);
    private MongoDAO mongoDAO;
    private JWTAuth provider;

    public ClaimPassCodeService(MongoClient dbClient, JWTAuth provider){
        this.mongoDAO = new MongoDAO(dbClient);
        this.provider = provider;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        LOGGER.debug("get a "+ Collections.PassCode+" {}",
                routingContext.request()
                        .absoluteURI());

        String passCodeParam = routingContext.request().getParam("passcode");
        JsonObject passCodeQuery = new JsonObject().put("passCode", passCodeParam);

        Future<JsonObject> future = mongoDAO.retrieveOne(Collections.PassCode, passCodeQuery);

        JsonObject response = new JsonObject();
        routingContext.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON);

        future.setHandler(result -> {
            if(future.succeeded() && !future.result().isEmpty()){

                JsonObject passCode = future.result();
                passCode.put("status", "claimed"); // claimed, unclaimed
                mongoDAO.delete(Collections.PassCode, passCodeQuery);

                JsonObject claims = new JsonObject()
                        .put("phoneNumber", passCode.getString("phoneNumber"))
                        .put("role", passCode.getString("role"));

                String token = provider.generateToken(claims, new JWTOptions().setAlgorithm("ES256"));

                response.put("success", "Admin logged in succesfully");
                routingContext.response().headers().add("Authorization", "Bearer " + token);
                routingContext.response().headers().add("Location", "https://admin.afecam.org/dashboard");
                routingContext.response().setStatusCode(HttpURLConnection.HTTP_OK);

            }else {
                response.put("error", "Invalid Pass Code");
                routingContext.response().setStatusCode(HttpURLConnection.HTTP_NOT_ACCEPTABLE);
            }

            routingContext.response().end(response.encode());
        });
    }
}