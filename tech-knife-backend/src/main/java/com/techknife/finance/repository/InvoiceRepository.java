package com.techknife.finance.repository;

import com.techknife.finance.entity.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends MongoRepository<Invoice, String> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByCustomerId(String customerId);

    List<Invoice> findByStatus(String status);

    boolean existsByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByInvoiceNumberContainingIgnoreCaseOrCustomerNameContainingIgnoreCase(String invoiceNumber, String customerName);

    List<Invoice> findByDueDateBeforeAndStatusNotIn(LocalDate date, List<String> statuses);

    List<Invoice> findByIssueDateBetween(LocalDate startDate, LocalDate endDate);
}
