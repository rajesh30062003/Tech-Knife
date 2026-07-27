package com.techknife.finance.repository;

import com.techknife.finance.entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    Optional<Payment> findByPaymentNumber(String paymentNumber);

    List<Payment> findByInvoiceId(String invoiceId);

    List<Payment> findByVendorId(String vendorId);

    boolean existsByPaymentNumber(String paymentNumber);

    List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
}
