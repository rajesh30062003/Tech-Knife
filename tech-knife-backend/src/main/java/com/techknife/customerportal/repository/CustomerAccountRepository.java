package com.techknife.customerportal.repository;

import com.techknife.customerportal.entity.CustomerAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerAccountRepository extends MongoRepository<CustomerAccount, String> {

    Optional<CustomerAccount> findByEmail(String email);

    Optional<CustomerAccount> findByCustomerCode(String customerCode);

    Optional<CustomerAccount> findByVerificationToken(String verificationToken);

    Optional<CustomerAccount> findByPasswordResetToken(String passwordResetToken);

    boolean existsByEmail(String email);
}
