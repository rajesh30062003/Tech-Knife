package com.techknife.iam.service;

import com.techknife.iam.dto.request.SendOtpRequest;
import com.techknife.iam.dto.request.VerifyOtpRequest;

/**
 * Service contract for multi-factor OTP generation, delivery, verification, and retry rate limiting.
 */
public interface OtpService {

    String generateOtp(String userId, String purpose);

    void sendOtp(SendOtpRequest request);

    boolean verifyOtp(VerifyOtpRequest request);

    void expireOtp(String userId, String purpose);

    boolean isMaxRetryExceeded(String userId, String purpose);
}
