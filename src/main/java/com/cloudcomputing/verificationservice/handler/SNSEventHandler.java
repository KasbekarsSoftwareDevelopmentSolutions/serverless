package com.cloudcomputing.verificationservice.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.cloudcomputing.verificationservice.VerificationServiceApplication;
import com.cloudcomputing.verificationservice.model.VerificationToken;
import com.cloudcomputing.verificationservice.service.EmailService;
import com.cloudcomputing.verificationservice.service.VerificationLinkService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

public class SNSEventHandler implements RequestHandler<SNSEvent, Boolean> {

  private LambdaLogger logger;

  @Autowired
  private VerificationLinkService verificationLinkService;

  @Autowired
  private EmailService emailService;

  @Override
  public Boolean handleRequest(SNSEvent event, Context context) {
    logger = context.getLogger();

    // Log environment variables for development
    VerificationServiceApplication.logEnvironmentVariables(context);

    // Log incoming SNS Event
    logger.log("Received SNS Event with " + event.getRecords().size() + " record(s).\n");

    try {
      // Process each record
      event.getRecords().forEach(record -> {
        logger.log("Processing record: " + record.getSNS().getMessage() + "\n");
        processRecord(record.getSNS().getMessage());
      });
    } catch (Exception e) {
      logger.log("Error during event processing: " + e.getMessage() + "\n");
      throw new RuntimeException(e);
    }

    return true;
  }

  private void processRecord(String message) {
    try {
      logger.log("Extracting details from message: " + message + "\n");

      String userEmail = extractFromMessage(message, "email");
      String userId = extractFromMessage(message, "userId");

      logger.log("Extracted email: " + userEmail + ", userId: " + userId + "\n");

      // Generate verification token
      VerificationToken token = verificationLinkService.generateVerificationToken(userEmail, userId);
      logger.log("Generated verification token: " + token.getToken() + "\n");

      // Create verification link
      String verificationLink = verificationLinkService.createVerificationLink(token);
      logger.log("Constructed verification link: " + verificationLink + "\n");

      // Send verification email
      emailService.sendVerificationEmail(userEmail, verificationLink);
      logger.log("Verification email sent to: " + userEmail + "\n");

    } catch (Exception e) {
      logger.log("Error processing record: " + e.getMessage() + "\n");
      throw new RuntimeException(e);
    }
  }

  private String extractFromMessage(String message, String key) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode rootNode = mapper.readTree(message);
    return rootNode.get(key).asText();
  }
}