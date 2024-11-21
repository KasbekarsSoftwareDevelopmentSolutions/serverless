package com.cloudcomputing.verificationservice.service;

import com.cloudcomputing.verificationservice.config.MailgunConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  @Autowired
  private MailgunConfig mailgunConfig;

  public void sendVerificationEmail(String email, String verificationLink) {
    String subject = "Verify your account";
    String body = String.format("Click the link to verify your account: %s", verificationLink);
    mailgunConfig.sendEmail(email, subject, body);
  }
}