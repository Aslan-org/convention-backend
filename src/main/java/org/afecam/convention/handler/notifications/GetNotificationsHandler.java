package org.afecam.convention.handler.notifications;

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
import io.vertx.core.json.JsonArray;

import java.net.HttpURLConnection;

public class GetNotificationsHandler implements Handler<RoutingContext> {
    private final static Logger LOGGER = LoggerFactory.getLogger(GetNotificationsHandler.class);
    private MongoDAO mongoDAO;
  
    public GetNotificationsHandler(MongoClient dbclient){
      this.mongoDAO = new MongoDAO(dbclient);
    }
  
    @Override
    public void handle(RoutingContext routingContext) {
      LOGGER.debug("get a batch of "+ Collections.Notification +"s {}",
        routingContext.request()
          .absoluteURI());
  
 
      JsonObject query = routingContext.getBodyAsJson();
      Future<JsonArray> future = mongoDAO.search(Collections.Notification, query);
  
      JsonObject response = new JsonObject();
      routingContext.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON);
  
      future.setHandler(result -> {
        if(future.succeeded()){
          response.put("success", Collections.Notification + "s Retrieved");
          response.put("data", future.result());
          routingContext.response().setStatusCode(HttpURLConnection.HTTP_OK);
        }else{
          response.put("error", Collections.Notification + "s Not Retrieved");
          routingContext.response().setStatusCode(HttpURLConnection.HTTP_NOT_FOUND);
        }
  
        routingContext.response().end(response.encode());
      });
    }
}
  
  