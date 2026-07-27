package com.techknife.customerportal.service;

import com.techknife.customerportal.dto.*;

public interface CustomerAuthService {

    CustomerLoginResponse login(CustomerLoginRequest request);

    CustomerLoginResponse register(CustomerRegisterRequest request);

    void forgotPassword(CustomerForgotPasswordRequest request);

    void resetPassword(CustomerResetPasswordRequest request);

    void verifyEmail(String token);

    void changePassword(String customerAccountId, CustomerChangePasswordRequest request);
}
