package com.techknife.customerportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLoginResponse {

    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private String customerAccountId;
    private String customerCode;
    private String email;
    private String companyName;
    private String contactPersonName;
    private List<String> roles;
    private List<String> permissions;
}
