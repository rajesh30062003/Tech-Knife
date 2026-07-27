package com.techknife.customerportal.repository;

import com.techknife.customerportal.entity.CustomerMilestone;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerMilestoneRepository extends MongoRepository<CustomerMilestone, String> {

    List<CustomerMilestone> findByProjectId(String projectId);

    List<CustomerMilestone> findByCustomerAccountId(String customerAccountId);

    List<CustomerMilestone> findByCustomerAccountIdAndStatus(String customerAccountId, String status);
}
