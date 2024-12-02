package org.cloudComputing.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudComputing.service.MailgunService;

public class VerificationHandler implements RequestHandler<SNSEvent, String> {

  private static final String BASE_URL = System.getenv("BASE_URL");
  private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper

  @Override
  public String handleRequest(SNSEvent event, Context context) {

    LambdaLogger logger = context.getLogger();
    logger.log("Starting VerificationHandler for SNS Event.\n");

    try {
      MailgunService mailgunService = new MailgunService();
      logger.log("MailgunService initialized.\n");

      for (SNSEvent.SNSRecord record : event.getRecords()) {

        logger.log("Processing SNS record: " + record.getSNS().getMessageId() + "\n");
        // Deserialize SNS message into POJO
        String message = record.getSNS().getMessage();
        logger.log("SNS message: " + message + "\n");

        UserVerificationRequest request = objectMapper.readValue(message, UserVerificationRequest.class);
        logger.log("Deserialized message to UserVerificationRequest: userId=" + request.getUserId() +
            ", emailAddress=" + request.getEmailAddress() +
            ", token=" + request.getToken() + "\n");

        // Construct the verification URL
        String verificationUrl = String.format("%s/v1/user/verify?user=%s&token=%s", BASE_URL, request.getUserId(),
            request.getToken());
        logger.log("Constructed verification URL: " + verificationUrl + "\n");

        // Construct email subject and body
        String emailSubject = "Email Verification";
        String emailBody = String.format(
            "Hello,\n\nPlease verify your email by clicking the link below:\n%s\n\nThank you!", verificationUrl);
        logger.log("Constructed email subject and body.\n");

        // Send email
        mailgunService.sendEmail(logger, request.getEmailAddress(), emailSubject, emailBody);
        logger.log("Email sent to: " + request.getEmailAddress() + "\n");
      }
      return "Emails processed successfully.";
    } catch (Exception e) {
      logger.log("Error occurred: " + e.getMessage() + "\n");
      return "Error processing emails: " + e.getMessage();
    }
  }

  // Inner POJO for deserialization
  public static class UserVerificationRequest {
    private String userId;

    @JsonProperty("userEmailAddress")
    private String userEmailAddress;

    @JsonProperty("userFirstName")
    private String userFirstName;
    private String token;

    // Getters and Setters
    public String getUserId() {
      return userId;
    }

    public void setUserId(String userId) {
      this.userId = userId;
    }

    public String getEmailAddress() {
      return userEmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
      this.userEmailAddress = emailAddress;
    }

    public String getFirstName() {
      return userFirstName;
    }

    public void setFirstName(String firstName) {
      this.userFirstName = firstName;
    }

    public String getToken() {
      return token;
    }

    public void setToken(String token) {
      this.token = token;
    }
  }
}
