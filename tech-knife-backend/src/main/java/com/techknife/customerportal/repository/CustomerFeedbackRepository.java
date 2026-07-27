package com.techknife.customerportal.repository;

import com.techknife.customerportal.entity.CustomerFeedback;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerFeedbackRepository extends MongoRepository<CustomerFeedback, String> {

    List<CustomerFeedback> findByCustomerAccountId(String customerAccountId);

    List<CustomerFeedback> findByProjectId(String projectId);
}
