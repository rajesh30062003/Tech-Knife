package com.techknife.finance.service;

import com.techknife.finance.dto.InvoiceDTO;

import java.util.List;

public interface InvoiceService {

    List<InvoiceDTO> getAllInvoices();

    List<InvoiceDTO> getInvoicesByCustomer(String customerId);

    InvoiceDTO getInvoiceById(String id);

    InvoiceDTO createInvoice(InvoiceDTO dto);

    InvoiceDTO updateInvoice(String id, InvoiceDTO dto);

    InvoiceDTO updateInvoiceStatus(String id, String status);

    void cancelInvoice(String id);
}
