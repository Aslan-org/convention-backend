package org.afecam.convention.services;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mongo.MongoClient;
import org.afecam.convention.dao.MongoDAO;
import org.afecam.convention.dto.Collections;

public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    private MailClient mailClient;
    private MongoDAO mongoDAO;

    public EmailService(MailClient mailClient, MongoClient dbClient) {
        this.mailClient = mailClient;
        this.mongoDAO = new MongoDAO(dbClient);
    }

    public void sendRoutine(){

        JsonObject query = new JsonObject()
                .put("type", "email")
                .put("status", "new");

        Future<JsonArray> future = mongoDAO.search(Collections.Notification, query);
        future.setHandler(result -> {
            if(future.succeeded() && !future.result().isEmpty()){
                for(Object object: future.result()){
                    JsonObject email = (JsonObject) object;
                    Future<JsonObject> emailFuture = send(email);

                    emailFuture.setHandler( emailResult -> {
                        if(emailFuture.succeeded()){
                            LOGGER.info("email sent successfully");
                            updateNotification(emailFuture.result());
                        }else{
                            LOGGER.info("email not sent");
                        }
                    });
                }
            }else{

            }
        });

    }

    public Future<JsonObject> send(JsonObject email) {
        Future<JsonObject> future = Future.future();

        LOGGER.info("sending email to " + email.getString("emailAddress"));

        MailMessage message = new MailMessage();
        message.setFrom("kraulain@gmail.com");
        message.setTo(email.getString("emailAddress"));
        message.setSubject(email.getString("title"));
        message.setHtml(email.getString("body"));

        mailClient.sendMail(message, result -> {
            if (result.succeeded()) {
                future.complete(email);
            } else {
                future.fail(result.cause());
            }
        });

        return future;
    }

    void updateNotification(JsonObject notification){
        JsonObject query = new JsonObject().put("_id", notification.getString("_id"));
        notification.put("status", "sent");

        Future<JsonObject> future = mongoDAO.update(Collections.Notification, query, notification);

        future.setHandler(result -> {
            if (future.succeeded()) {
                LOGGER.info("Notification marked as sent");
            } else {
                LOGGER.info("Couldn't update Notification");
            }
        });
    }

}
