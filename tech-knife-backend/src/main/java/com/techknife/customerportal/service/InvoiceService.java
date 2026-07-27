package com.techknife.customerportal.service;

import com.techknife.customerportal.dto.InvoiceViewDTO;
import com.techknife.customerportal.dto.PaymentHistoryDTO;

import java.util.List;

public interface InvoiceService {

    List<InvoiceViewDTO> getInvoices(String customerAccountId, String status);

    InvoiceViewDTO getInvoiceById(String invoiceId, String customerAccountId);

    List<PaymentHistoryDTO> getPaymentHistory(String customerAccountId, String invoiceId);

    InvoiceViewDTO createInvoice(InvoiceViewDTO dto);

    PaymentHistoryDTO recordPayment(PaymentHistoryDTO dto);
}
