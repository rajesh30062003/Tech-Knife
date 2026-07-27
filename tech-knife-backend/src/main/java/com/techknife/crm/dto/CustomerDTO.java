package com.techknife.crm.dto;

import com.techknife.crm.entity.CustomerAddress;
import com.techknife.crm.entity.CustomerContact;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private String id;
    private String customerCode;

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String gstNumber;
    private String pan;
    private String industry;
    private String businessType;
    private String website;

    private CustomerContact primaryContact;
    private List<CustomerContact> contacts;
    private CustomerAddress billingAddress;
    private CustomerAddress shippingAddress;

    private String accountManagerId;
    private String accountManagerName;
    private String status;

    private Instant createdAt;
    private Instant updatedAt;
}
