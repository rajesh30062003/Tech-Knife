package com.techknife.customerportal.repository;

import com.techknife.customerportal.entity.CustomerTaskView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerTaskViewRepository extends MongoRepository<CustomerTaskView, String> {

    List<CustomerTaskView> findByProjectId(String projectId);

    List<CustomerTaskView> findByCustomerAccountId(String customerAccountId);

    List<CustomerTaskView> findByCustomerAccountIdAndStatus(String customerAccountId, String status);

    long countByCustomerAccountIdAndStatus(String customerAccountId, String status);
}
