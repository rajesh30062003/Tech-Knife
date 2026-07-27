package com.techknife.customerportal.repository;

import com.techknife.customerportal.entity.PaymentHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentHistoryRepository extends MongoRepository<PaymentHistory, String> {

    List<PaymentHistory> findByCustomerAccountId(String customerAccountId);

    List<PaymentHistory> findByInvoiceId(String invoiceId);
}
