package com.techknife.customerportal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customer_accounts")
public class CustomerAccount {

    @Id
    private String id;

    @Indexed(unique = true)
    private String customerCode;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String companyName;

    private String contactPersonName;

    private String phone;

    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, SUSPENDED, PENDING_VERIFICATION

    @Builder.Default
    private Boolean emailVerified = false;

    private String verificationToken;

    private String passwordResetToken;

    private Instant passwordResetExpiry;

    private Instant lastLoginAt;

    @Builder.Default
    private List<String> roles = List.of("ROLE_CUSTOMER");

    @Builder.Default
    private List<String> permissions = List.of(
            "CUSTOMER_PORTAL_ACCESS",
            "CUSTOMER_PROJECT_VIEW",
            "CUSTOMER_TICKET_CREATE",
            "CUSTOMER_TICKET_REPLY",
            "CUSTOMER_DOCUMENT_DOWNLOAD",
            "CUSTOMER_INVOICE_VIEW"
    );

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
