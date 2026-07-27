package com.techknife.crm.service;

import com.techknife.crm.dto.QuotationDTO;
import com.techknife.crm.entity.Quotation;
import com.techknife.crm.entity.QuotationItem;
import com.techknife.crm.repository.QuotationRepository;
import com.techknife.storage.FileStorageService;
import com.techknife.storage.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final FileStorageService fileStorageService;

    public List<QuotationDTO> getAllQuotations(String status) {
        List<Quotation> list;
        if (status != null && !status.isEmpty()) {
            list = quotationRepository.findByApprovalStatus(status.toUpperCase());
        } else {
            list = quotationRepository.findAll();
        }
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public QuotationDTO getQuotationById(String id) {
        Quotation q = quotationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quotation not found with id: " + id));
        return mapToDTO(q);
    }

    public QuotationDTO createQuotation(QuotationDTO dto) {
        String qNumber = "QTN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        List<QuotationItem> items = dto.getItems() != null ? dto.getItems() : List.of();
        double subTotal = 0.0;
        double discountTotal = 0.0;
        double taxTotal = 0.0;

        for (QuotationItem item : items) {
            double qty = item.getQuantity() != null ? item.getQuantity() : 1;
            double price = item.getUnitPrice() != null ? item.getUnitPrice() : 0.0;
            double disc = item.getDiscount() != null ? item.getDiscount() : 0.0;
            double tax = item.getTax() != null ? item.getTax() : 0.0;

            double itemTotal = (qty * price) - disc + tax;
            item.setTotalPrice(itemTotal);

            subTotal += (qty * price);
            discountTotal += disc;
            taxTotal += tax;
        }

        double grandTotal = subTotal - discountTotal + taxTotal;

        Quotation q = Quotation.builder()
                .quotationNumber(qNumber)
                .customerId(dto.getCustomerId())
                .opportunityId(dto.getOpportunityId())
                .leadId(dto.getLeadId())
                .title(dto.getTitle())
                .items(items)
                .subTotal(subTotal)
                .discountTotal(discountTotal)
                .taxTotal(taxTotal)
                .grandTotal(grandTotal)
                .validityDate(dto.getValidityDate())
                .approvalStatus(dto.getApprovalStatus() != null ? dto.getApprovalStatus() : "PENDING")
                .approvedBy(dto.getApprovedBy())
                .notes(dto.getNotes())
                .build();

        Quotation saved = quotationRepository.save(q);
        log.info("Created Quotation: {} for customer {}", saved.getQuotationNumber(), saved.getCustomerId());
        return mapToDTO(saved);
    }

    public QuotationDTO uploadQuotationDocument(String id, MultipartFile file) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quotation not found with id: " + id));

        FileUploadResponse response = fileStorageService.uploadDocument(file, "crm/quotations");
        quotation.setQuotationUrl(response.getSecureUrl());

        Quotation saved = quotationRepository.save(quotation);
        return mapToDTO(saved);
    }

    public QuotationDTO updateApprovalStatus(String id, String status, String approvedBy) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quotation not found with id: " + id));

        quotation.setApprovalStatus(status.toUpperCase());
        if (approvedBy != null) {
            quotation.setApprovedBy(approvedBy);
        }

        Quotation saved = quotationRepository.save(quotation);
        return mapToDTO(saved);
    }

    public void deleteQuotation(String id) {
        if (!quotationRepository.existsById(id)) {
            throw new RuntimeException("Quotation not found with id: " + id);
        }
        quotationRepository.deleteById(id);
    }

    public QuotationDTO mapToDTO(Quotation q) {
        return QuotationDTO.builder()
                .id(q.getId())
                .quotationNumber(q.getQuotationNumber())
                .customerId(q.getCustomerId())
                .opportunityId(q.getOpportunityId())
                .leadId(q.getLeadId())
                .title(q.getTitle())
                .items(q.getItems())
                .subTotal(q.getSubTotal())
                .discountTotal(q.getDiscountTotal())
                .taxTotal(q.getTaxTotal())
                .grandTotal(q.getGrandTotal())
                .validityDate(q.getValidityDate())
                .approvalStatus(q.getApprovalStatus())
                .approvedBy(q.getApprovedBy())
                .quotationUrl(q.getQuotationUrl())
                .notes(q.getNotes())
                .createdAt(q.getCreatedAt())
                .updatedAt(q.getUpdatedAt())
                .build();
    }
}
