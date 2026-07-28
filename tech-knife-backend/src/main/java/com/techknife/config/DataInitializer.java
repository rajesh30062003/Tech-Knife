package com.techknife.config;

import com.techknife.backend.constant.Role;
import com.techknife.iam.enums.AccountStatus;
import com.techknife.iam.enums.AccountType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

/**
 * Seeds default Super Admin account into MongoDB Atlas 'users' collection on application startup
 * if the database is empty or the administrator record does not exist.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final com.techknife.iam.repository.UserRepository iamUserRepository;
    @org.springframework.beans.factory.annotation.Qualifier("backendUserRepository")
    private final com.techknife.backend.repository.UserRepository backendUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            seedDefaultSuperAdmin();
        } catch (Exception ex) {
            log.error("Error during MongoDB Atlas Super Admin seeding: {}", ex.getMessage(), ex);
        }
    }

    private void seedDefaultSuperAdmin() {
        String adminEmail = "admin@techknife.com";
        String adminUsername = "admin";
        String rawPassword = "Admin@123";

        boolean iamAdminExists = iamUserRepository.existsByOfficialEmail(adminEmail) || iamUserRepository.existsByUserId(adminUsername);
        boolean backendAdminExists = backendUserRepository.existsByEmail(adminEmail);

        if (iamAdminExists && backendAdminExists) {
            log.info("Super Admin account ('{}') already exists in MongoDB Atlas.", adminEmail);
            return;
        }

        log.info("Seeding default Super Admin account ('{}') into MongoDB Atlas 'users' collection...", adminEmail);
        String encodedPassword = passwordEncoder.encode(rawPassword);
        Instant now = Instant.now();

        if (!iamAdminExists) {
            com.techknife.iam.entity.User iamAdmin = com.techknife.iam.entity.User.builder()
                    .userId(adminUsername)
                    .officialEmail(adminEmail)
                    .personalEmail("admin.personal@techknife.com")
                    .password(encodedPassword)
                    .firstName("Super")
                    .lastName("Admin")
                    .designation("Super Administrator")
                    .department("Executive Management")
                    .employeeId("EMP-00001")
                    .accountStatus(AccountStatus.ACTIVE)
                    .accountType(AccountType.INTERNAL)
                    .emailVerified(true)
                    .mobileVerified(true)
                    .roles(Set.of("ROLE_SUPER_ADMIN", "ROLE_ADMIN"))
                    .permissions(Set.of("USER_READ", "USER_WRITE", "PROJECT_READ", "PROJECT_WRITE", "PAYROLL_READ", "PAYROLL_WRITE", "CRM_READ", "CRM_WRITE", "RECRUITMENT_READ", "RECRUITMENT_WRITE"))
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            iamUserRepository.save(iamAdmin);
            log.info("Successfully persisted IAM Super Admin user ('{}') into MongoDB Atlas.", adminUsername);
        }

        if (!backendAdminExists) {
            com.techknife.backend.entity.User backendAdmin = com.techknife.backend.entity.User.builder()
                    .email(adminEmail)
                    .password(encodedPassword)
                    .firstName("Super")
                    .lastName("Admin")
                    .designation("Super Administrator")
                    .department("Executive Management")
                    .phoneNumber("+1234567890")
                    .avatarUrl("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=250")
                    .enabled(true)
                    .accountNonLocked(true)
                    .emailVerified(true)
                    .roles(Set.of(Role.ROLE_SUPER_ADMIN))
                    .build();

            backendUserRepository.save(backendAdmin);
            log.info("Successfully persisted Backend Super Admin user ('{}') into MongoDB Atlas.", adminEmail);
        }
    }
}
