package com.techknife.finance.service.impl;

import com.techknife.finance.dto.PaymentDTO;
import com.techknife.finance.entity.Invoice;
import com.techknife.finance.entity.Payment;
import com.techknife.finance.repository.InvoiceRepository;
import com.techknife.finance.repository.PaymentRepository;
import com.techknife.finance.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDTO> getPaymentsByInvoice(String invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDTO> getPaymentsByVendor(String vendorId) {
        return paymentRepository.findByVendorId(vendorId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentDTO getPaymentById(String id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with id: " + id));
        return mapToDTO(payment);
    }

    @Override
    public PaymentDTO recordPayment(PaymentDTO dto) {
        String num = dto.getPaymentNumber() != null && !dto.getPaymentNumber().isBlank()
                ? dto.getPaymentNumber()
                : "PAY-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Payment payment = Payment.builder()
                .paymentNumber(num)
                .invoiceId(dto.getInvoiceId())
                .vendorId(dto.getVendorId())
                .expenseId(dto.getExpenseId())
                .entityName(dto.getEntityName())
                .paymentDate(dto.getPaymentDate() != null ? dto.getPaymentDate() : LocalDate.now())
                .amount(dto.getAmount())
                .paymentMethod(dto.getPaymentMethod() != null ? dto.getPaymentMethod() : "BANK_TRANSFER")
                .referenceNumber(dto.getReferenceNumber())
                .status(dto.getStatus() != null ? dto.getStatus() : "COMPLETED")
                .notes(dto.getNotes())
                .build();

        // If payment is against an invoice, update invoice paid amount and status
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

        Payment saved = paymentRepository.save(payment);
        return mapToDTO(saved);
    }

    private PaymentDTO mapToDTO(Payment p) {
        return PaymentDTO.builder()
                .id(p.getId())
                .paymentNumber(p.getPaymentNumber())
                .invoiceId(p.getInvoiceId())
                .vendorId(p.getVendorId())
                .expenseId(p.getExpenseId())
                .entityName(p.getEntityName())
                .paymentDate(p.getPaymentDate())
                .amount(p.getAmount())
                .paymentMethod(p.getPaymentMethod())
                .referenceNumber(p.getReferenceNumber())
                .status(p.getStatus())
                .notes(p.getNotes())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .createdBy(p.getCreatedBy())
                .updatedBy(p.getUpdatedBy())
                .build();
    }
}
