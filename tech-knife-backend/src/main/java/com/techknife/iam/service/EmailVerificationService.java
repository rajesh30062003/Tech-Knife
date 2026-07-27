package com.techknife.iam.service;

import com.techknife.iam.dto.request.ResendVerificationRequest;
import com.techknife.iam.dto.request.VerifyEmailRequest;

/**
 * Service contract for user registration email verification token lifecycle.
 */
public interface EmailVerificationService {

    String generateVerificationToken(String userId);

    boolean verifyEmail(VerifyEmailRequest request);

    void resendVerificationEmail(ResendVerificationRequest request);
}
