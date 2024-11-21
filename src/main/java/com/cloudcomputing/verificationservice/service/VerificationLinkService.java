package com.cloudcomputing.verificationservice.service;

import com.cloudcomputing.verificationservice.model.VerificationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VerificationLinkService {

  @Value("${base.url}")
  private String baseUrl;

  public VerificationToken generateVerificationToken(String userEmail, String userId) {
    VerificationToken token = new VerificationToken();
    token.setToken(UUID.randomUUID().toString());
    token.setUserEmail(userEmail);
    token.setUserId(userId);
    token.setExpiryDate(LocalDateTime.now().plusDays(1));
    token.setVerificationFlag(false);
    return token;
  }

  public String createVerificationLink(VerificationToken token) {
    return String.format("%s/verify?user=%s&token=%s", baseUrl, token.getUserEmail(), token.getToken());
  }
}