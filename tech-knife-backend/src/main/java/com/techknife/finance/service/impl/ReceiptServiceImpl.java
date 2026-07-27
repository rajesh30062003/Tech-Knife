package com.techknife.finance.service.impl;

import com.techknife.finance.dto.ReceiptDTO;
import com.techknife.finance.entity.Invoice;
import com.techknife.finance.entity.Receipt;
import com.techknife.finance.repository.InvoiceRepository;
import com.techknife.finance.repository.ReceiptRepository;
import com.techknife.finance.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    public List<ReceiptDTO> getAllReceipts() {
        return receiptRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReceiptDTO> getReceiptsByCustomer(String customerId) {
        return receiptRepository.findByCustomerId(customerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReceiptDTO> getReceiptsByInvoice(String invoiceId) {
        return receiptRepository.findByInvoiceId(invoiceId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReceiptDTO getReceiptById(String id) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Receipt not found with id: " + id));
        return mapToDTO(receipt);
    }

    @Override
    public ReceiptDTO issueReceipt(ReceiptDTO dto) {
        String num = dto.getReceiptNumber() != null && !dto.getReceiptNumber().isBlank()
                ? dto.getReceiptNumber()
                : "RCT-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Receipt receipt = Receipt.builder()
                .receiptNumber(num)
                .receiptType(dto.getReceiptType() != null ? dto.getReceiptType() : "CUSTOMER_RECEIPT")
                .customerId(dto.getCustomerId())
                .customerName(dto.getCustomerName())
                .invoiceId(dto.getInvoiceId())
                .receiptDate(dto.getReceiptDate() != null ? dto.getReceiptDate() : LocalDate.now())
                .amount(dto.getAmount())
                .paymentMethod(dto.getPaymentMethod() != null ? dto.getPaymentMethod() : "BANK_TRANSFER")
                .referenceNumber(dto.getReferenceNumber())
                .status(dto.getStatus() != null ? dto.getStatus() : "COMPLETED")
                .notes(dto.getNotes())
                .build();

        // Update invoice if applicable
        if (dto.getInvoiceId() != null) {
            Invoice inv = invoiceRepository.findById(dto.getInvoiceId()).orElse(null);
            if (inv != null) {
                BigDecimal newPaid = inv.getPaidAmount().add(dto.getAmount());
                BigDecimal newBalance = inv.getTotalAmount().subtract(newPaid);
                inv.setPaidAmount(newPaid);
                inv.setBalanceDue(newBalance.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : newBalance);

                if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
                    inv.setStatus("PAID");
                } else {
                    inv.setStatus("PARTIALLY_PAID");
                }
                invoiceRepository.save(inv);
            }
        }

        Receipt saved = receiptRepository.save(receipt);
        return mapToDTO(saved);
    }

    private ReceiptDTO mapToDTO(Receipt r) {
        return ReceiptDTO.builder()
                .id(r.getId())
                .receiptNumber(r.getReceiptNumber())
                .receiptType(r.getReceiptType())
                .customerId(r.getCustomerId())
                .customerName(r.getCustomerName())
                .invoiceId(r.getInvoiceId())
                .receiptDate(r.getReceiptDate())
                .amount(r.getAmount())
                .paymentMethod(r.getPaymentMethod())
                .referenceNumber(r.getReferenceNumber())
                .status(r.getStatus())
                .notes(r.getNotes())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .createdBy(r.getCreatedBy())
                .updatedBy(r.getUpdatedBy())
                .build();
    }
}
