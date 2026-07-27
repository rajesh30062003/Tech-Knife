package com.techknife.customerportal.repository;

import com.techknife.customerportal.entity.CustomerProject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerProjectRepository extends MongoRepository<CustomerProject, String> {

    List<CustomerProject> findByCustomerAccountId(String customerAccountId);

    List<CustomerProject> findByCustomerAccountIdAndStatus(String customerAccountId, String status);

    Optional<CustomerProject> findByIdAndCustomerAccountId(String id, String customerAccountId);

    long countByCustomerAccountIdAndStatus(String customerAccountId, String status);

    long countByCustomerAccountId(String customerAccountId);

    List<CustomerProject> findByProjectNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String desc);
}
