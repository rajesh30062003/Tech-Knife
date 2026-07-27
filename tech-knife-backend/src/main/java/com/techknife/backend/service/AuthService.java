package com.techknife.backend.service;

import com.techknife.backend.dto.*;

public interface AuthService {

    AuthResponse authenticateUser(AuthRequest request);

    AuthResponse registerUser(RegisterRequest request);

    AuthResponse refreshToken(String refreshToken);

    void logout(String refreshToken);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void sendEmailVerificationOtp(SendOtpRequest request);

    boolean verifyEmailOtp(VerifyOtpRequest request);

    void changePassword(String userId, ChangePasswordRequest request);

    UserResponse getCurrentUser(String userId);

    UserResponse updateProfilePicture(String userId, ProfilePictureRequest request);
}
