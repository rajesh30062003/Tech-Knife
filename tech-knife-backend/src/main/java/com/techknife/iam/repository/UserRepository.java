package com.techknife.iam.repository;

import com.techknife.iam.entity.User;
import com.techknife.iam.enums.AccountStatus;
import com.techknife.iam.enums.AccountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for User entity operations, authentication queries, and IAM lookup.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String>, UserSearchRepository {

    Optional<User> findByOfficialEmail(String officialEmail);

    Optional<User> findByPersonalEmail(String personalEmail);

    Optional<User> findByUserId(String userId);

    boolean existsByOfficialEmail(String officialEmail);

    boolean existsByUserId(String userId);

    List<User> findByAccountStatus(AccountStatus accountStatus);

    Page<User> findByAccountStatus(AccountStatus accountStatus, Pageable pageable);

    List<User> findByAccountType(AccountType accountType);

    Page<User> findByAccountType(AccountType accountType, Pageable pageable);

    List<User> findByRoles(String role);

    List<User> findByRolesContaining(String role);

    List<User> findByEmailVerified(boolean emailVerified);

    List<User> findByMobileVerified(boolean mobileVerified);

    @Query("{ 'accountLocked': true }")
    List<User> findLockedAccounts();

    @Query("{ '$or': [ { 'firstName': { '$regex': ?0, '$options': 'i' } }, { 'lastName': { '$regex': ?0, '$options': 'i' } } ] }")
    List<User> searchByName(String keyword);

    @Query("{ '$or': [ { 'firstName': { '$regex': ?0, '$options': 'i' } }, { 'lastName': { '$regex': ?0, '$options': 'i' } } ] }")
    Page<User> searchByName(String keyword, Pageable pageable);

    @Query("{ 'department': ?0 }")
    List<User> searchByDepartment(String department);

    @Query("{ 'department': ?0 }")
    Page<User> searchByDepartment(String department, Pageable pageable);

    @Query("{ 'managerId': ?0 }")
    List<User> searchByManager(String managerId);

    @Query("{ 'managerId': ?0 }")
    Page<User> searchByManager(String managerId, Pageable pageable);

    @Query("{ 'lastLogin': { '$gte': ?0 } }")
    List<User> findRecentlyLoggedIn(Instant since);
}
