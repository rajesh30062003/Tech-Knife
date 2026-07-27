package com.techknife.customerportal.service.impl;

import com.techknife.customerportal.dto.CustomerProfileDTO;
import com.techknife.customerportal.entity.CustomerAccount;
import com.techknife.customerportal.entity.CustomerProfile;
import com.techknife.customerportal.repository.CustomerAccountRepository;
import com.techknife.customerportal.repository.CustomerProfileRepository;
import com.techknife.customerportal.service.CustomerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerProfileServiceImpl implements CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;
    private final CustomerAccountRepository customerAccountRepository;

    @Override
    public CustomerProfileDTO getProfile(String customerAccountId) {
        CustomerProfile profile = customerProfileRepository.findByCustomerAccountId(customerAccountId)
                .orElseGet(() -> {
                    CustomerAccount account = customerAccountRepository.findById(customerAccountId)
                            .orElseThrow(() -> new IllegalArgumentException("Customer account not found: " + customerAccountId));
                    CustomerProfile newProfile = CustomerProfile.builder()
                            .customerAccountId(account.getId())
                            .companyName(account.getCompanyName())
                            .contactName(account.getContactPersonName())
                            .contactEmail(account.getEmail())
                            .phone(account.getPhone())
                            .build();
                    return customerProfileRepository.save(newProfile);
                });

        return mapToDTO(profile);
    }

    @Override
    public CustomerProfileDTO updateProfile(String customerAccountId, CustomerProfileDTO dto) {
        CustomerProfile profile = customerProfileRepository.findByCustomerAccountId(customerAccountId)
                .orElseGet(() -> CustomerProfile.builder().customerAccountId(customerAccountId).build());

        if (dto.getCompanyName() != null) profile.setCompanyName(dto.getCompanyName());
        if (dto.getContactName() != null) profile.setContactName(dto.getContactName());
        if (dto.getContactEmail() != null) profile.setContactEmail(dto.getContactEmail());
        if (dto.getPhone() != null) profile.setPhone(dto.getPhone());
        if (dto.getSecondaryPhone() != null) profile.setSecondaryPhone(dto.getSecondaryPhone());
        if (dto.getAddressLine1() != null) profile.setAddressLine1(dto.getAddressLine1());
        if (dto.getAddressLine2() != null) profile.setAddressLine2(dto.getAddressLine2());
        if (dto.getCity() != null) profile.setCity(dto.getCity());
        if (dto.getState() != null) profile.setState(dto.getState());
        if (dto.getCountry() != null) profile.setCountry(dto.getCountry());
        if (dto.getPostalCode() != null) profile.setPostalCode(dto.getPostalCode());
        if (dto.getTaxId() != null) profile.setTaxId(dto.getTaxId());
        if (dto.getWebsite() != null) profile.setWebsite(dto.getWebsite());
        if (dto.getCompanySize() != null) profile.setCompanySize(dto.getCompanySize());
        if (dto.getIndustry() != null) profile.setIndustry(dto.getIndustry());
        if (dto.getAvatarUrl() != null) profile.setAvatarUrl(dto.getAvatarUrl());
        if (dto.getPreferredLanguage() != null) profile.setPreferredLanguage(dto.getPreferredLanguage());
        if (dto.getTimezone() != null) profile.setTimezone(dto.getTimezone());

        CustomerProfile saved = customerProfileRepository.save(profile);

        customerAccountRepository.findById(customerAccountId).ifPresent(account -> {
            if (dto.getCompanyName() != null) account.setCompanyName(dto.getCompanyName());
            if (dto.getContactName() != null) account.setContactPersonName(dto.getContactName());
            if (dto.getPhone() != null) account.setPhone(dto.getPhone());
            customerAccountRepository.save(account);
        });

        return mapToDTO(saved);
    }

    private CustomerProfileDTO mapToDTO(CustomerProfile profile) {
        return CustomerProfileDTO.builder()
                .id(profile.getId())
                .customerAccountId(profile.getCustomerAccountId())
                .companyName(profile.getCompanyName())
                .contactName(profile.getContactName())
                .contactEmail(profile.getContactEmail())
                .phone(profile.getPhone())
                .secondaryPhone(profile.getSecondaryPhone())
                .addressLine1(profile.getAddressLine1())
                .addressLine2(profile.getAddressLine2())
                .city(profile.getCity())
                .state(profile.getState())
                .country(profile.getCountry())
                .postalCode(profile.getPostalCode())
                .taxId(profile.getTaxId())
                .website(profile.getWebsite())
                .companySize(profile.getCompanySize())
                .industry(profile.getIndustry())
                .avatarUrl(profile.getAvatarUrl())
                .preferredLanguage(profile.getPreferredLanguage())
                .timezone(profile.getTimezone())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
