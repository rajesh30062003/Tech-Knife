package com.techknife.iam.repository;

import com.techknife.iam.entity.User;
import com.techknife.iam.enums.AccountStatus;
import com.techknife.iam.enums.AccountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

/**
 * Custom repository interface defining dynamic search, filtering, and pagination for User entities.
 */
public interface UserSearchRepository {

    Page<User> searchUsers(String keyword,
                           AccountStatus status,
                           AccountType accountType,
                           Boolean emailVerified,
                           Boolean mobileVerified,
                           Boolean accountLocked,
                           Set<String> roles,
                           String department,
                           String managerId,
                           Pageable pageable);
}
