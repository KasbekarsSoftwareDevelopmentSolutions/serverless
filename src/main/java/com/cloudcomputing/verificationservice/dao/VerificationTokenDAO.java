package com.cloudcomputing.verificationservice.dao;

import com.cloudcomputing.verificationservice.model.VerificationToken;

public interface VerificationTokenDAO {
  void saveVerificationToken(VerificationToken token);
}