package com.example;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.gson.Gson;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class NotificationSender {
  // [START send_notification_df]
  final class Notification {

    private final String title;

    Notification(String title) {
      this.title = title;
    }

    String getTitle() {
      return title;
    }
  }

  final class Target {

    private final String userId;
    private final String intent;
    private final String locale;

    Target(String userId, String intent, String locale) {
      this.userId = userId;
      this.intent = intent;
      this.locale = locale;
    }

    String getUserId() {
      return userId;
    }

    String getIntent() {
      return intent;
    }

    String getLocale() {
      return locale;
    }
  }

  final class PushMessage {

    private final Notification userNotification;
    private final Target target;

    PushMessage(Notification userNotification, Target target) {
      this.userNotification = userNotification;
      this.target = target;
    }

    Notification getUserNotification() {
      return userNotification;
    }

    Target getTarget() {
      return target;
    }
  }

  final class PushNotification {

    private final PushMessage customPushMessage;
    private boolean isInSandbox;

    PushNotification(PushMessage customPushMessage, boolean isInSandbox) {
      this.customPushMessage = customPushMessage;
      this.isInSandbox = isInSandbox;
    }

    PushMessage getCustomPushMessage() {
      return customPushMessage;
    }

    boolean getIsInSandbox() {
      return isInSandbox;
    }
  }

  private PushNotification createNotification(String title, String userId, String intent, String locale) {
    Notification notification = new Notification(title);
    Target target = new Target(userId, intent, locale);
    PushMessage message = new PushMessage(notification, target);
    boolean isInSandbox = true;
    return new PushNotification(message, isInSandbox);
  }

  private ServiceAccountCredentials loadCredentials() throws IOException {
    String actionsApiServiceAccountFile =
        this.getClass().getClassLoader().getResource("service-account.json").getFile();
    InputStream actionsApiServiceAccount = new FileInputStream(actionsApiServiceAccountFile);
    ServiceAccountCredentials serviceAccountCredentials =
        ServiceAccountCredentials.fromStream(actionsApiServiceAccount);
    return (ServiceAccountCredentials)
        serviceAccountCredentials.createScoped(
            Collections.singleton(
                "https://www.googleapis.com/auth/actions.fulfillment.conversation"));
  }

  private String getAccessToken() throws IOException {
    AccessToken token = loadCredentials().refreshAccessToken();
    return token.getTokenValue();
  }

  public void sendNotification(String title, String userId, String intent, String locale) throws IOException {
    Preconditions.checkNotNull(title, "title cannot be null.");
    Preconditions.checkNotNull(userId, "userId cannot be null.");
    Preconditions.checkNotNull(intent, "intent cannot be null.");
    Preconditions.checkNotNull(locale, "locale cannot be null");
    PushNotification notification = createNotification(title, userId, intent, locale);

    HttpPost request = new HttpPost("https://actions.googleapis.com/v2/conversations:send");

    String token = getAccessToken();

    request.setHeader("Content-type", "application/json");
    request.setHeader("Authorization", "Bearer " + token);

    StringEntity entity = new StringEntity(new Gson().toJson(notification));
    entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
    request.setEntity(entity);
    HttpClient httpClient = HttpClientBuilder.create().build();
    httpClient.execute(request);
  }
  // [END send_notification_df]

  public static void main(String[] args) throws IOException {
    NotificationSender notificationSender = new NotificationSender();
    notificationSender.sendNotification("Push Notification Title", "<UPDATES_USER_ID>", "Notification Intent", "en-US");
  }

}
