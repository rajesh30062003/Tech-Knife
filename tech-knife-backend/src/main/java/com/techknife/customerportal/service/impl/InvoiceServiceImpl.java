package com.techknife.customerportal.service.impl;

import com.techknife.customerportal.dto.InvoiceViewDTO;
import com.techknife.customerportal.dto.PaymentHistoryDTO;
import com.techknife.customerportal.entity.InvoiceView;
import com.techknife.customerportal.entity.PaymentHistory;
import com.techknife.customerportal.repository.InvoiceViewRepository;
import com.techknife.customerportal.repository.PaymentHistoryRepository;
import com.techknife.customerportal.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceViewRepository invoiceViewRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    @Override
    public List<InvoiceViewDTO> getInvoices(String customerAccountId, String status) {
        List<InvoiceView> invoices;
        if (status != null && !status.isBlank()) {
            invoices = invoiceViewRepository.findByCustomerAccountIdAndStatus(customerAccountId, status.toUpperCase());
        } else {
            invoices = invoiceViewRepository.findByCustomerAccountId(customerAccountId);
        }

        return invoices.stream().map(i -> mapToDTO(i, false)).collect(Collectors.toList());
    }

    @Override
    public InvoiceViewDTO getInvoiceById(String invoiceId, String customerAccountId) {
        InvoiceView invoice = invoiceViewRepository.findByIdAndCustomerAccountId(invoiceId, customerAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found or access denied"));

        return mapToDTO(invoice, true);
    }

    @Override
    public List<PaymentHistoryDTO> getPaymentHistory(String customerAccountId, String invoiceId) {
        if (invoiceId != null && !invoiceId.isBlank()) {
            invoiceViewRepository.findByIdAndCustomerAccountId(invoiceId, customerAccountId)
                    .orElseThrow(() -> new IllegalArgumentException("Invoice not found or access denied"));
            return paymentHistoryRepository.findByInvoiceId(invoiceId).stream()
                    .map(this::mapPaymentToDTO)
                    .collect(Collectors.toList());
        } else {
            return paymentHistoryRepository.findByCustomerAccountId(customerAccountId).stream()
                    .map(this::mapPaymentToDTO)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public InvoiceViewDTO createInvoice(InvoiceViewDTO dto) {
        String invoiceNumber = dto.getInvoiceNumber() != null ? dto.getInvoiceNumber() : "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        InvoiceView invoice = InvoiceView.builder()
                .invoiceNumber(invoiceNumber)
                .customerAccountId(dto.getCustomerAccountId())
                .customerName(dto.getCustomerName())
                .projectId(dto.getProjectId())
                .projectName(dto.getProjectName())
                .amount(dto.getAmount() != null ? dto.getAmount() : 0.0)
                .taxAmount(dto.getTaxAmount() != null ? dto.getTaxAmount() : 0.0)
                .totalAmount(dto.getTotalAmount() != null ? dto.getTotalAmount() : (dto.getAmount() != null ? dto.getAmount() : 0.0) + (dto.getTaxAmount() != null ? dto.getTaxAmount() : 0.0))
                .currency(dto.getCurrency() != null ? dto.getCurrency() : "USD")
                .status(dto.getStatus() != null ? dto.getStatus() : "UNPAID")
                .issueDate(dto.getIssueDate())
                .dueDate(dto.getDueDate())
                .pdfUrl(dto.getPdfUrl())
                .lineItems(dto.getLineItems())
                .build();

        InvoiceView saved = invoiceViewRepository.save(invoice);
        return mapToDTO(saved, false);
    }

    @Override
    public PaymentHistoryDTO recordPayment(PaymentHistoryDTO dto) {
        InvoiceView invoice = invoiceViewRepository.findById(dto.getInvoiceId())
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + dto.getInvoiceId()));

        PaymentHistory payment = PaymentHistory.builder()
                .invoiceId(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .customerAccountId(invoice.getCustomerAccountId())
                .amount(dto.getAmount() != null ? dto.getAmount() : invoice.getTotalAmount())
                .paymentDate(dto.getPaymentDate())
                .paymentMethod(dto.getPaymentMethod() != null ? dto.getPaymentMethod() : "BANK_TRANSFER")
                .referenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "REF-" + System.currentTimeMillis() % 1000000)
                .status(dto.getStatus() != null ? dto.getStatus() : "SUCCESS")
                .notes(dto.getNotes())
                .build();

        PaymentHistory saved = paymentHistoryRepository.save(payment);

        if ("SUCCESS".equalsIgnoreCase(saved.getStatus())) {
            invoice.setStatus("PAID");
            invoiceViewRepository.save(invoice);
        }

        return mapPaymentToDTO(saved);
    }

    private InvoiceViewDTO mapToDTO(InvoiceView invoice, boolean includePayments) {
        InvoiceViewDTO dto = InvoiceViewDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .customerAccountId(invoice.getCustomerAccountId())
                .customerName(invoice.getCustomerName())
                .projectId(invoice.getProjectId())
                .projectName(invoice.getProjectName())
                .amount(invoice.getAmount())
                .taxAmount(invoice.getTaxAmount())
                .totalAmount(invoice.getTotalAmount())
                .currency(invoice.getCurrency())
                .status(invoice.getStatus())
                .issueDate(invoice.getIssueDate())
                .dueDate(invoice.getDueDate())
                .pdfUrl(invoice.getPdfUrl())
                .lineItems(invoice.getLineItems())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();

        if (includePayments) {
            List<PaymentHistoryDTO> payments = paymentHistoryRepository.findByInvoiceId(invoice.getId()).stream()
                    .map(this::mapPaymentToDTO)
                    .collect(Collectors.toList());
            dto.setPaymentHistory(payments);
        }

        return dto;
    }

    private PaymentHistoryDTO mapPaymentToDTO(PaymentHistory p) {
        return PaymentHistoryDTO.builder()
                .id(p.getId())
                .invoiceId(p.getInvoiceId())
                .invoiceNumber(p.getInvoiceNumber())
                .customerAccountId(p.getCustomerAccountId())
                .amount(p.getAmount())
                .paymentDate(p.getPaymentDate())
                .paymentMethod(p.getPaymentMethod())
                .referenceNumber(p.getReferenceNumber())
                .status(p.getStatus())
                .notes(p.getNotes())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
