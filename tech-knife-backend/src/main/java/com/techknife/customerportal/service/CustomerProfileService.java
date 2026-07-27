package com.techknife.customerportal.service;

import com.techknife.customerportal.dto.CustomerProfileDTO;

public interface CustomerProfileService {

    CustomerProfileDTO getProfile(String customerAccountId);

    CustomerProfileDTO updateProfile(String customerAccountId, CustomerProfileDTO dto);
}
