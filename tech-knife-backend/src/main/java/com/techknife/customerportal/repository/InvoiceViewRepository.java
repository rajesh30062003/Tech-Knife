package com.techknife.customerportal.repository;

import com.techknife.customerportal.entity.InvoiceView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceViewRepository extends MongoRepository<InvoiceView, String> {

    List<InvoiceView> findByCustomerAccountId(String customerAccountId);

    List<InvoiceView> findByCustomerAccountIdAndStatus(String customerAccountId, String status);

    Optional<InvoiceView> findByIdAndCustomerAccountId(String id, String customerAccountId);

    Optional<InvoiceView> findByInvoiceNumber(String invoiceNumber);

    long countByCustomerAccountIdAndStatus(String customerAccountId, String status);

    long countByCustomerAccountId(String customerAccountId);

    List<InvoiceView> findByCustomerAccountIdAndInvoiceNumberContainingIgnoreCase(String customerAccountId, String query);
}
