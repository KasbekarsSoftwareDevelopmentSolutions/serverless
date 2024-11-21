package com.cloudcomputing.verificationservice.repo;

import com.cloudcomputing.verificationservice.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
}