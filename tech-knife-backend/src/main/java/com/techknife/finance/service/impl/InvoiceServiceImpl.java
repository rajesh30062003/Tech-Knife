package com.techknife.finance.service.impl;

import com.techknife.finance.dto.InvoiceDTO;
import com.techknife.finance.dto.InvoiceItemDTO;
import com.techknife.finance.entity.Invoice;
import com.techknife.finance.entity.InvoiceItem;
import com.techknife.finance.repository.InvoiceRepository;
import com.techknife.finance.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    @Override
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDTO> getInvoicesByCustomer(String customerId) {
        return invoiceRepository.findByCustomerId(customerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceDTO getInvoiceById(String id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found with id: " + id));
        return mapToDTO(invoice);
    }

    @Override
    public InvoiceDTO createInvoice(InvoiceDTO dto) {
        if (dto.getInvoiceNumber() != null && invoiceRepository.existsByInvoiceNumber(dto.getInvoiceNumber())) {
            throw new IllegalArgumentException("Invoice number already exists: " + dto.getInvoiceNumber());
        }

        String invNum = dto.getInvoiceNumber() != null && !dto.getInvoiceNumber().isBlank()
                ? dto.getInvoiceNumber()
                : "INV-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        List<InvoiceItem> items = calculateItems(dto.getItems());
        BigDecimal subtotal = items.stream().map(InvoiceItem::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal taxTotal = items.stream().map(InvoiceItem::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = subtotal.add(taxTotal);

        BigDecimal paid = dto.getPaidAmount() != null ? dto.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal balance = total.subtract(paid);

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invNum)
                .customerId(dto.getCustomerId())
                .customerName(dto.getCustomerName())
                .customerEmail(dto.getCustomerEmail())
                .invoiceType(dto.getInvoiceType() != null ? dto.getInvoiceType() : "CUSTOMER_INVOICE")
                .issueDate(dto.getIssueDate() != null ? dto.getIssueDate() : LocalDate.now())
                .dueDate(dto.getDueDate() != null ? dto.getDueDate() : LocalDate.now().plusDays(30))
                .financialYearId(dto.getFinancialYearId())
                .costCenterId(dto.getCostCenterId())
                .items(items)
                .subtotal(subtotal)
                .taxTotal(taxTotal)
                .totalAmount(total)
                .paidAmount(paid)
                .balanceDue(balance)
                .status(dto.getStatus() != null ? dto.getStatus() : "DRAFT")
                .isRecurring(dto.getIsRecurring() != null ? dto.getIsRecurring() : false)
                .recurringFrequency(dto.getRecurringFrequency())
                .notes(dto.getNotes())
                .termsAndConditions(dto.getTermsAndConditions())
                .build();

        Invoice saved = invoiceRepository.save(invoice);
        return mapToDTO(saved);
    }

    @Override
    public InvoiceDTO updateInvoice(String id, InvoiceDTO dto) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found with id: " + id));

        if (dto.getCustomerId() != null) invoice.setCustomerId(dto.getCustomerId());
        if (dto.getCustomerName() != null) invoice.setCustomerName(dto.getCustomerName());
        if (dto.getCustomerEmail() != null) invoice.setCustomerEmail(dto.getCustomerEmail());
        if (dto.getIssueDate() != null) invoice.setIssueDate(dto.getIssueDate());
        if (dto.getDueDate() != null) invoice.setDueDate(dto.getDueDate());
        if (dto.getFinancialYearId() != null) invoice.setFinancialYearId(dto.getFinancialYearId());
        if (dto.getCostCenterId() != null) invoice.setCostCenterId(dto.getCostCenterId());
        if (dto.getNotes() != null) invoice.setNotes(dto.getNotes());
        if (dto.getTermsAndConditions() != null) invoice.setTermsAndConditions(dto.getTermsAndConditions());
        if (dto.getIsRecurring() != null) invoice.setIsRecurring(dto.getIsRecurring());
        if (dto.getRecurringFrequency() != null) invoice.setRecurringFrequency(dto.getRecurringFrequency());

        if (dto.getItems() != null) {
            List<InvoiceItem> items = calculateItems(dto.getItems());
            BigDecimal subtotal = items.stream().map(InvoiceItem::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal taxTotal = items.stream().map(InvoiceItem::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal total = subtotal.add(taxTotal);

            invoice.setItems(items);
            invoice.setSubtotal(subtotal);
            invoice.setTaxTotal(taxTotal);
            invoice.setTotalAmount(total);
            invoice.setBalanceDue(total.subtract(invoice.getPaidAmount()));
        }

        Invoice saved = invoiceRepository.save(invoice);
        return mapToDTO(saved);
    }

    @Override
    public InvoiceDTO updateInvoiceStatus(String id, String status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found with id: " + id));

        invoice.setStatus(status.toUpperCase());
        Invoice saved = invoiceRepository.save(invoice);
        return mapToDTO(saved);
    }

    @Override
    public void cancelInvoice(String id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found with id: " + id));

        invoice.setStatus("CANCELLED");
        invoiceRepository.save(invoice);
    }

    private List<InvoiceItem> calculateItems(List<InvoiceItemDTO> dtos) {
        if (dtos == null) return new ArrayList<>();
        return dtos.stream().map(dto -> {
            int qty = dto.getQuantity() != null ? dto.getQuantity() : 1;
            BigDecimal price = dto.getUnitPrice() != null ? dto.getUnitPrice() : BigDecimal.ZERO;
            BigDecimal taxRate = dto.getTaxRate() != null ? dto.getTaxRate() : BigDecimal.ZERO;

            BigDecimal sub = price.multiply(BigDecimal.valueOf(qty));
            BigDecimal tax = sub.multiply(taxRate).divide(BigDecimal.valueOf(100));
            BigDecimal total = sub.add(tax);

            return InvoiceItem.builder()
                    .itemName(dto.getItemName())
                    .description(dto.getDescription())
                    .quantity(qty)
                    .unitPrice(price)
                    .taxRate(taxRate)
                    .taxAmount(tax)
                    .totalAmount(sub) // net total before tax or total with tax
                    .build();
        }).collect(Collectors.toList());
    }

    private InvoiceDTO mapToDTO(Invoice inv) {
        List<InvoiceItemDTO> itemDTOs = inv.getItems() != null
                ? inv.getItems().stream().map(i -> InvoiceItemDTO.builder()
                .itemName(i.getItemName())
                .description(i.getDescription())
                .quantity(i.getQuantity())
                .unitPrice(i.getUnitPrice())
                .taxRate(i.getTaxRate())
                .taxAmount(i.getTaxAmount())
                .totalAmount(i.getTotalAmount())
                .build()).collect(Collectors.toList())
                : new ArrayList<>();

        return InvoiceDTO.builder()
                .id(inv.getId())
                .invoiceNumber(inv.getInvoiceNumber())
                .customerId(inv.getCustomerId())
                .customerName(inv.getCustomerName())
                .customerEmail(inv.getCustomerEmail())
                .invoiceType(inv.getInvoiceType())
                .issueDate(inv.getIssueDate())
                .dueDate(inv.getDueDate())
                .financialYearId(inv.getFinancialYearId())
                .costCenterId(inv.getCostCenterId())
                .items(itemDTOs)
                .subtotal(inv.getSubtotal())
                .taxTotal(inv.getTaxTotal())
                .totalAmount(inv.getTotalAmount())
                .paidAmount(inv.getPaidAmount())
                .balanceDue(inv.getBalanceDue())
                .status(inv.getStatus())
                .isRecurring(inv.getIsRecurring())
                .recurringFrequency(inv.getRecurringFrequency())
                .notes(inv.getNotes())
                .termsAndConditions(inv.getTermsAndConditions())
                .createdAt(inv.getCreatedAt())
                .updatedAt(inv.getUpdatedAt())
                .createdBy(inv.getCreatedBy())
                .updatedBy(inv.getUpdatedBy())
                .build();
    }
}
