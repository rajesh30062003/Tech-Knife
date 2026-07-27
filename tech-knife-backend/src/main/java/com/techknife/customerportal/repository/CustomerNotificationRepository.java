package com.techknife.customerportal.repository;

import com.techknife.customerportal.entity.CustomerNotification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerNotificationRepository extends MongoRepository<CustomerNotification, String> {

    List<CustomerNotification> findByCustomerAccountIdOrderByCreatedAtDesc(String customerAccountId);

    long countByCustomerAccountIdAndIsReadFalse(String customerAccountId);
}
