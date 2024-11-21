package com.cloudcomputing.verificationservice;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VerificationServiceApplication {
  public static void main(String[] args) {
    // Set environment variables as system properties
    setEnvironmentProperties();

    // Start Spring Boot application
    SpringApplication.run(VerificationServiceApplication.class, args);
  }

  private static void setEnvironmentProperties() {
    System.setProperty("spring.datasource.url", System.getenv("DB_ENDPOINT"));
    System.setProperty("spring.datasource.username", System.getenv("DB_USERNAME"));
    System.setProperty("spring.datasource.password", System.getenv("DB_PASSWORD"));
    System.setProperty("mailgun.api.key", System.getenv("MAILGUN_API_KEY"));
    System.setProperty("mailgun.domain", System.getenv("MAILGUN_DOMAIN"));
    System.setProperty("base.url", System.getenv("BASE_URL"));

    // Validate critical environment variables
    if (System.getenv("DB_ENDPOINT") == null || System.getenv("MAILGUN_API_KEY") == null ||
        System.getenv("MAILGUN_DOMAIN") == null || System.getenv("BASE_URL") == null) {
      throw new IllegalArgumentException("Required environment variables are missing.");
    }
  }

  public static void logEnvironmentVariables(Context context) {
    LambdaLogger logger = context.getLogger();
    logger.log("---- Logging Environment Variables ----\n");

    Map<String, String> env = System.getenv();
    env.forEach((key, value) -> {
      // Mask sensitive information like passwords
      if (key.equalsIgnoreCase("DB_PASSWORD") || key.equalsIgnoreCase("MAILGUN_API_KEY")) {
        logger.log(key + ": [REDACTED]\n");
      } else {
        logger.log(key + ": " + value + "\n");
      }
    });

    logger.log("---- End of Environment Variables ----\n");
  }
}