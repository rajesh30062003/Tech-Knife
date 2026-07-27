package com.techknife.finance.service;

import com.techknife.finance.dto.ReceiptDTO;

import java.util.List;

public interface ReceiptService {

    List<ReceiptDTO> getAllReceipts();

    List<ReceiptDTO> getReceiptsByCustomer(String customerId);

    List<ReceiptDTO> getReceiptsByInvoice(String invoiceId);

    ReceiptDTO getReceiptById(String id);

    ReceiptDTO issueReceipt(ReceiptDTO dto);
}
