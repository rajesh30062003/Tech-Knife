package com.techknife.procurement.service.impl;

import com.techknife.procurement.dto.PurchaseApprovalDTO;
import com.techknife.procurement.dto.PurchaseRequestDTO;
import com.techknife.procurement.dto.PurchaseRequestItemDTO;
import com.techknife.procurement.entity.PurchaseApproval;
import com.techknife.procurement.entity.PurchaseRequest;
import com.techknife.procurement.entity.PurchaseRequestItem;
import com.techknife.procurement.repository.PurchaseApprovalRepository;
import com.techknife.procurement.repository.PurchaseRequestRepository;
import com.techknife.procurement.service.PurchaseRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseRequestServiceImpl implements PurchaseRequestService {

    private final PurchaseRequestRepository purchaseRequestRepository;
    private final PurchaseApprovalRepository purchaseApprovalRepository;

    @Override
    public List<PurchaseRequestDTO> getAllPurchaseRequests() {
        return purchaseRequestRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PurchaseRequestDTO getPurchaseRequestById(String id) {
        PurchaseRequest pr = purchaseRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Purchase request not found with id: " + id));
        return mapToDTO(pr);
    }

    @Override
    public PurchaseRequestDTO createPurchaseRequest(PurchaseRequestDTO dto) {
        if (purchaseRequestRepository.existsByRequestNumber(dto.getRequestNumber())) {
            throw new IllegalArgumentException("Purchase request already exists with number: " + dto.getRequestNumber());
        }

        List<PurchaseRequestItem> items = new ArrayList<>();
        BigDecimal totalEst = BigDecimal.ZERO;

        if (dto.getItems() != null) {
            for (PurchaseRequestItemDTO itemDto : dto.getItems()) {
                BigDecimal unitPrice = itemDto.getEstimatedUnitPrice() != null ? itemDto.getEstimatedUnitPrice() : BigDecimal.ZERO;
                int qty = itemDto.getQuantity() != null ? itemDto.getQuantity() : 1;
                BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(qty));
                totalEst = totalEst.add(totalPrice);

                items.add(PurchaseRequestItem.builder()
                        .itemId(itemDto.getItemId())
                        .itemCode(itemDto.getItemCode())
                        .itemName(itemDto.getItemName())
                        .quantity(qty)
                        .estimatedUnitPrice(unitPrice)
                        .estimatedTotalPrice(totalPrice)
                        .remarks(itemDto.getRemarks())
                        .build());
            }
        }

        PurchaseRequest pr = PurchaseRequest.builder()
                .requestNumber(dto.getRequestNumber())
                .requestedById(dto.getRequestedById())
                .requestedByName(dto.getRequestedByName())
                .departmentId(dto.getDepartmentId())
                .departmentName(dto.getDepartmentName())
                .requestDate(dto.getRequestDate() != null ? dto.getRequestDate() : LocalDate.now())
                .requiredDate(dto.getRequiredDate())
                .priority(dto.getPriority() != null ? dto.getPriority() : "MEDIUM")
                .status("PENDING")
                .items(items)
                .totalEstimatedAmount(totalEst)
                .justification(dto.getJustification())
                .build();

        PurchaseRequest saved = purchaseRequestRepository.save(pr);
        return mapToDTO(saved);
    }

    @Override
    public PurchaseRequestDTO updatePurchaseRequest(String id, PurchaseRequestDTO dto) {
        PurchaseRequest pr = purchaseRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Purchase request not found with id: " + id));

        if (dto.getRequiredDate() != null) pr.setRequiredDate(dto.getRequiredDate());
        if (dto.getPriority() != null) pr.setPriority(dto.getPriority());
        if (dto.getJustification() != null) pr.setJustification(dto.getJustification());
        if (dto.getStatus() != null) pr.setStatus(dto.getStatus());

        if (dto.getItems() != null) {
            List<PurchaseRequestItem> items = new ArrayList<>();
            BigDecimal totalEst = BigDecimal.ZERO;
            for (PurchaseRequestItemDTO itemDto : dto.getItems()) {
                BigDecimal unitPrice = itemDto.getEstimatedUnitPrice() != null ? itemDto.getEstimatedUnitPrice() : BigDecimal.ZERO;
                int qty = itemDto.getQuantity() != null ? itemDto.getQuantity() : 1;
                BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(qty));
                totalEst = totalEst.add(totalPrice);

                items.add(PurchaseRequestItem.builder()
                        .itemId(itemDto.getItemId())
                        .itemCode(itemDto.getItemCode())
                        .itemName(itemDto.getItemName())
                        .quantity(qty)
                        .estimatedUnitPrice(unitPrice)
                        .estimatedTotalPrice(totalPrice)
                        .remarks(itemDto.getRemarks())
                        .build());
            }
            pr.setItems(items);
            pr.setTotalEstimatedAmount(totalEst);
        }

        PurchaseRequest saved = purchaseRequestRepository.save(pr);
        return mapToDTO(saved);
    }

    @Override
    public PurchaseApprovalDTO approvePurchaseRequest(String id, String approverId, String approverName, boolean approved, String comments) {
        PurchaseRequest pr = purchaseRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Purchase request not found with id: " + id));

        String newStatus = approved ? "APPROVED" : "REJECTED";
        pr.setStatus(newStatus);
        purchaseRequestRepository.save(pr);

        PurchaseApproval approval = PurchaseApproval.builder()
                .purchaseRequestId(pr.getId())
                .requestNumber(pr.getRequestNumber())
                .approverId(approverId)
                .approverName(approverName)
                .approvalStep(1)
                .status(newStatus)
                .comments(comments)
                .approvalDate(LocalDate.now())
                .build();

        PurchaseApproval saved = purchaseApprovalRepository.save(approval);
        return mapToApprovalDTO(saved);
    }

    @Override
    public List<PurchaseApprovalDTO> getApprovalsByPurchaseRequest(String purchaseRequestId) {
        return purchaseApprovalRepository.findByPurchaseRequestId(purchaseRequestId).stream()
                .map(this::mapToApprovalDTO)
                .collect(Collectors.toList());
    }

    private PurchaseRequestDTO mapToDTO(PurchaseRequest pr) {
        List<PurchaseRequestItemDTO> itemDtos = pr.getItems() != null ? pr.getItems().stream()
                .map(i -> PurchaseRequestItemDTO.builder()
                        .itemId(i.getItemId())
                        .itemCode(i.getItemCode())
                        .itemName(i.getItemName())
                        .quantity(i.getQuantity())
                        .estimatedUnitPrice(i.getEstimatedUnitPrice())
                        .estimatedTotalPrice(i.getEstimatedTotalPrice())
                        .remarks(i.getRemarks())
                        .build())
                .collect(Collectors.toList()) : new ArrayList<>();

        return PurchaseRequestDTO.builder()
                .id(pr.getId())
                .requestNumber(pr.getRequestNumber())
                .requestedById(pr.getRequestedById())
                .requestedByName(pr.getRequestedByName())
                .departmentId(pr.getDepartmentId())
                .departmentName(pr.getDepartmentName())
                .requestDate(pr.getRequestDate())
                .requiredDate(pr.getRequiredDate())
                .priority(pr.getPriority())
                .status(pr.getStatus())
                .items(itemDtos)
                .totalEstimatedAmount(pr.getTotalEstimatedAmount())
                .justification(pr.getJustification())
                .createdAt(pr.getCreatedAt())
                .updatedAt(pr.getUpdatedAt())
                .createdBy(pr.getCreatedBy())
                .updatedBy(pr.getUpdatedBy())
                .build();
    }

    private PurchaseApprovalDTO mapToApprovalDTO(PurchaseApproval a) {
        return PurchaseApprovalDTO.builder()
                .id(a.getId())
                .purchaseRequestId(a.getPurchaseRequestId())
                .requestNumber(a.getRequestNumber())
                .approverId(a.getApproverId())
                .approverName(a.getApproverName())
                .approvalStep(a.getApprovalStep())
                .status(a.getStatus())
                .comments(a.getComments())
                .approvalDate(a.getApprovalDate())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
