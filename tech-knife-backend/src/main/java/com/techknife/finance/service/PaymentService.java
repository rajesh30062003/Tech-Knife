package com.techknife.finance.service;

import com.techknife.finance.dto.PaymentDTO;

import java.util.List;

public interface PaymentService {

    List<PaymentDTO> getAllPayments();

    List<PaymentDTO> getPaymentsByInvoice(String invoiceId);

    List<PaymentDTO> getPaymentsByVendor(String vendorId);

    PaymentDTO getPaymentById(String id);

    PaymentDTO recordPayment(PaymentDTO dto);
}
