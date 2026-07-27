package com.techknife.customerportal.repository;

import com.techknife.customerportal.entity.CustomerProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerProfileRepository extends MongoRepository<CustomerProfile, String> {

    Optional<CustomerProfile> findByCustomerAccountId(String customerAccountId);
}
