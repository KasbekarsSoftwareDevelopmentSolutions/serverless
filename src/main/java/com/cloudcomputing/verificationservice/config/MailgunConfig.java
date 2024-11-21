package com.cloudcomputing.verificationservice.config;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import com.mailgun.model.message.Message;
import com.mailgun.model.message.MessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailgunConfig {

  @Value("${mailgun.api.key}")
  private String apiKey;

  @Value("${mailgun.domain}")
  private String domain;

  public void sendEmail(String to, String subject, String body) {
    // Initialize Mailgun client
    MailgunMessagesApi mailgunMessagesApi = MailgunClient.config(apiKey)
      .createApi(MailgunMessagesApi.class);

    // Build the email message
    Message message = Message.builder()
      .from("no-reply@" + domain)
      .to(to)
      .subject(subject)
      .text(body)
      .build();

    // Send the email
    MessageResponse response = mailgunMessagesApi.sendMessage(domain, message);

    // Check response status and handle errors
    if (response.getMessage().equalsIgnoreCase("Queued")) {
      System.out.println("Email successfully sent: " + response.getId());
    } else {
      throw new RuntimeException("Failed to send email: " + response.getMessage());
    }
  }
}