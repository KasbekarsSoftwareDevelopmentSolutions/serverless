package org.cloudComputing.service;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import com.mailgun.model.message.Message;
import com.mailgun.model.message.MessageResponse;

public class MailgunService {

  // Mailgun API configurations (replace with your details)
  private static final String MAILGUN_API_KEY = System.getenv("MAILGUN_API_KEY");
  private static final String MAILGUN_DOMAIN = System.getenv("MAILGUN_DOMAIN");

  private final MailgunMessagesApi mailgunMessagesApi;

  public MailgunService() {
    // Initialize Mailgun client
    this.mailgunMessagesApi = MailgunClient.config(MAILGUN_API_KEY).createApi(MailgunMessagesApi.class);
  }

  public void sendEmail(LambdaLogger logger, String to, String subject, String body) {
    logger.log("Inside MailgunService Preparing to send email to: " + to + "\n");
    Message message = Message.builder()
      .from("Verification Service <no-reply@" + MAILGUN_DOMAIN + ">")
      .to(to)
      .subject(subject)
      .text(body)
      .build();

    // Send the email using Mailgun API
    try {
      MessageResponse response = mailgunMessagesApi.sendMessage(MAILGUN_DOMAIN, message);
      logger.log("Mailgun response: " + response.getMessage() + "\n");

      if (response.getMessage() == null) {
        throw new RuntimeException("Failed to send email via Mailgun API");
      }

      logger.log("Email sent successfully to: " + to + "\n");
    } catch (Exception e) {
      logger.log("Error sending email: " + e.getMessage() + "\n");
      throw new RuntimeException(e);
    }
  }
}