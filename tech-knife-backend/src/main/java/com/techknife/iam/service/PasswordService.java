package com.techknife.iam.service;

import com.techknife.iam.dto.request.ChangePasswordRequest;
import com.techknife.iam.dto.request.ForgotPasswordRequest;
import com.techknife.iam.dto.request.ResetPasswordRequest;

/**
 * Service contract for password management workflows including recovery, reset, change, and strength validation.
 */
public interface PasswordService {

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(String userId, ChangePasswordRequest request);

    boolean validatePasswordStrength(String password);

    boolean validatePasswordHistory(String userId, String newPassword);
}
