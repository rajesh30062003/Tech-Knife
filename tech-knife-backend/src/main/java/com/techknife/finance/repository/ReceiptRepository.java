package com.techknife.finance.repository;

import com.techknife.finance.entity.Receipt;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends MongoRepository<Receipt, String> {

    Optional<Receipt> findByReceiptNumber(String receiptNumber);

    List<Receipt> findByCustomerId(String customerId);

    List<Receipt> findByInvoiceId(String invoiceId);

    boolean existsByReceiptNumber(String receiptNumber);

    List<Receipt> findByReceiptDateBetween(LocalDate startDate, LocalDate endDate);
}
