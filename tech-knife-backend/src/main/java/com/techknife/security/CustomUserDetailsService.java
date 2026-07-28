package com.techknife.security;

import com.techknife.backend.entity.User;
import com.techknife.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    @org.springframework.beans.factory.annotation.Qualifier("backendUserRepository")
    private final com.techknife.backend.repository.UserRepository backendUserRepository;

    private final com.techknife.iam.repository.UserRepository iamUserRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new UsernameNotFoundException("Username or email identifier is required");
        }

        String search = identifier.trim().toLowerCase();

        // 1. Check backend UserRepository by email
        Optional<com.techknife.backend.entity.User> backendUser = backendUserRepository.findByEmail(search);
        if (backendUser.isPresent()) {
            return UserPrincipal.create(backendUser.get());
        }

        // 2. Check IAM UserRepository by officialEmail, personalEmail, or userId
        Optional<com.techknife.iam.entity.User> iamUser = iamUserRepository.findByOfficialEmail(search);
        if (iamUser.isEmpty()) {
            iamUser = iamUserRepository.findByPersonalEmail(search);
        }
        if (iamUser.isEmpty()) {
            iamUser = iamUserRepository.findByUserId(search);
        }

        if (iamUser.isPresent()) {
            com.techknife.iam.entity.User u = iamUser.get();
            return UserPrincipal.create(
                    u.getUserId() != null ? u.getUserId() : u.getId(),
                    u.getOfficialEmail() != null ? u.getOfficialEmail() : u.getPersonalEmail(),
                    u.getPassword(),
                    u.getAccountStatus() == com.techknife.iam.enums.AccountStatus.ACTIVE || u.getAccountStatus() == com.techknife.iam.enums.AccountStatus.PENDING_VERIFICATION,
                    !u.isAccountLocked(),
                    u.getRoles() != null ? new ArrayList<>(u.getRoles()) : new ArrayList<>(),
                    u.getPermissions() != null ? u.getPermissions() : new HashSet<>()
            );
        }

        throw new UsernameNotFoundException("User not found in MongoDB Atlas with email/username: " + identifier);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(String id) {
        Optional<com.techknife.backend.entity.User> backendUser = backendUserRepository.findById(id);
        if (backendUser.isPresent()) {
            return UserPrincipal.create(backendUser.get());
        }

        Optional<com.techknife.iam.entity.User> iamUser = iamUserRepository.findByUserId(id);
        if (iamUser.isEmpty()) {
            iamUser = iamUserRepository.findById(id);
        }

        if (iamUser.isPresent()) {
            com.techknife.iam.entity.User u = iamUser.get();
            return UserPrincipal.create(
                    u.getUserId() != null ? u.getUserId() : u.getId(),
                    u.getOfficialEmail() != null ? u.getOfficialEmail() : u.getPersonalEmail(),
                    u.getPassword(),
                    u.getAccountStatus() == com.techknife.iam.enums.AccountStatus.ACTIVE || u.getAccountStatus() == com.techknife.iam.enums.AccountStatus.PENDING_VERIFICATION,
                    !u.isAccountLocked(),
                    u.getRoles() != null ? new ArrayList<>(u.getRoles()) : new ArrayList<>(),
                    u.getPermissions() != null ? u.getPermissions() : new HashSet<>()
            );
        }

        throw new UsernameNotFoundException("User not found in MongoDB Atlas with id: " + id);
    }
}
