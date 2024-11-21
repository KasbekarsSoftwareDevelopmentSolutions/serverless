package com.cloudcomputing.verificationservice.dao.implementation;

import com.cloudcomputing.verificationservice.dao.VerificationTokenDAO;
import com.cloudcomputing.verificationservice.model.VerificationToken;
import com.cloudcomputing.verificationservice.repo.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class VerificationTokenDAOImpl implements VerificationTokenDAO {

  @Autowired
  private VerificationTokenRepository repository;

  @Override
  public void saveVerificationToken(VerificationToken token) {
    repository.save(token);
  }
}