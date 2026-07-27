package com.techknife.customerportal.service.impl;

import com.techknife.customerportal.dto.SupportTicketDTO;
import com.techknife.customerportal.dto.TicketReplyDTO;
import com.techknife.customerportal.entity.CustomerAccount;
import com.techknife.customerportal.entity.SupportTicket;
import com.techknife.customerportal.entity.TicketReply;
import com.techknife.customerportal.repository.CustomerAccountRepository;
import com.techknife.customerportal.repository.SupportTicketRepository;
import com.techknife.customerportal.repository.TicketReplyRepository;
import com.techknife.customerportal.service.SupportTicketService;
import com.techknife.storage.FileStorageService;
import com.techknife.storage.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final TicketReplyRepository ticketReplyRepository;
    private final CustomerAccountRepository customerAccountRepository;
    private final FileStorageService fileStorageService;

    @Override
    public List<SupportTicketDTO> getTickets(String customerAccountId, String status) {
        List<SupportTicket> tickets;
        if (status != null && !status.isBlank()) {
            tickets = supportTicketRepository.findByCustomerAccountIdAndStatus(customerAccountId, status.toUpperCase());
        } else {
            tickets = supportTicketRepository.findByCustomerAccountId(customerAccountId);
        }

        return tickets.stream()
                .map(t -> mapToDTO(t, false))
                .collect(Collectors.toList());
    }

    @Override
    public SupportTicketDTO getTicketById(String id, String customerAccountId) {
        SupportTicket ticket = supportTicketRepository.findByIdAndCustomerAccountId(id, customerAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found or access denied"));

        return mapToDTO(ticket, true);
    }

    @Override
    public SupportTicketDTO createTicket(String customerAccountId, SupportTicketDTO dto, MultipartFile[] files) {
        CustomerAccount account = customerAccountRepository.findById(customerAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Customer account not found"));

        String ticketNumber = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        List<SupportTicket.Attachment> attachmentList = processFileUploads(files);

        SupportTicket ticket = SupportTicket.builder()
                .ticketNumber(ticketNumber)
                .customerAccountId(customerAccountId)
                .customerName(account.getContactPersonName())
                .customerEmail(account.getEmail())
                .projectId(dto.getProjectId())
                .projectName(dto.getProjectName())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory() != null ? dto.getCategory().toUpperCase() : "GENERAL")
                .priority(dto.getPriority() != null ? dto.getPriority().toUpperCase() : "MEDIUM")
                .status("OPEN")
                .attachments(attachmentList)
                .build();

        SupportTicket saved = supportTicketRepository.save(ticket);
        return mapToDTO(saved, true);
    }

    @Override
    public SupportTicketDTO addReply(String ticketId, String customerAccountId, TicketReplyDTO replyDTO, MultipartFile[] files) {
        SupportTicket ticket = supportTicketRepository.findByIdAndCustomerAccountId(ticketId, customerAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found or access denied"));

        CustomerAccount account = customerAccountRepository.findById(customerAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Customer account not found"));

        List<SupportTicket.Attachment> attachmentList = processFileUploads(files);

        TicketReply reply = TicketReply.builder()
                .ticketId(ticketId)
                .senderType("CUSTOMER")
                .senderId(customerAccountId)
                .senderName(account.getContactPersonName())
                .senderEmail(account.getEmail())
                .message(replyDTO.getMessage())
                .attachments(attachmentList)
                .build();

        ticketReplyRepository.save(reply);

        if ("WAITING_ON_CUSTOMER".equalsIgnoreCase(ticket.getStatus()) || "RESOLVED".equalsIgnoreCase(ticket.getStatus())) {
            ticket.setStatus("IN_PROGRESS");
            supportTicketRepository.save(ticket);
        }

        return mapToDTO(ticket, true);
    }

    @Override
    public SupportTicketDTO updateTicketStatus(String ticketId, String customerAccountId, String status, String resolutionNotes) {
        SupportTicket ticket = supportTicketRepository.findByIdAndCustomerAccountId(ticketId, customerAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found or access denied"));

        ticket.setStatus(status.toUpperCase());
        if (resolutionNotes != null) {
            ticket.setResolutionNotes(resolutionNotes);
        }

        if ("CLOSED".equalsIgnoreCase(status) || "RESOLVED".equalsIgnoreCase(status)) {
            ticket.setClosedAt(Instant.now());
        }

        SupportTicket saved = supportTicketRepository.save(ticket);
        return mapToDTO(saved, true);
    }

    @Override
    public SupportTicketDTO closeTicket(String ticketId, String customerAccountId) {
        return updateTicketStatus(ticketId, customerAccountId, "CLOSED", "Closed by customer");
    }

    @Override
    public SupportTicketDTO reopenTicket(String ticketId, String customerAccountId) {
        SupportTicket ticket = supportTicketRepository.findByIdAndCustomerAccountId(ticketId, customerAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found or access denied"));

        ticket.setStatus("OPEN");
        ticket.setReopenedAt(Instant.now());
        SupportTicket saved = supportTicketRepository.save(ticket);
        return mapToDTO(saved, true);
    }

    private List<SupportTicket.Attachment> processFileUploads(MultipartFile[] files) {
        List<SupportTicket.Attachment> attachments = new ArrayList<>();
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    try {
                        FileUploadResponse upload = fileStorageService.uploadFile(file, "customer_portal/tickets");
                        attachments.add(SupportTicket.Attachment.builder()
                                .fileName(upload.getOriginalFilename())
                                .fileUrl(upload.getSecureUrl())
                                .publicId(upload.getPublicId())
                                .fileSize(upload.getBytes())
                                .contentType(file.getContentType())
                                .build());
                    } catch (Exception e) {
                        log.error("Failed to upload ticket attachment: {}", e.getMessage());
                    }
                }
            }
        }
        return attachments;
    }

    private SupportTicketDTO mapToDTO(SupportTicket ticket, boolean includeReplies) {
        SupportTicketDTO dto = SupportTicketDTO.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .customerAccountId(ticket.getCustomerAccountId())
                .customerName(ticket.getCustomerName())
                .customerEmail(ticket.getCustomerEmail())
                .projectId(ticket.getProjectId())
                .projectName(ticket.getProjectName())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .category(ticket.getCategory())
                .priority(ticket.getPriority())
                .status(ticket.getStatus())
                .assignedToId(ticket.getAssignedToId())
                .assignedToName(ticket.getAssignedToName())
                .resolutionNotes(ticket.getResolutionNotes())
                .attachments(ticket.getAttachments())
                .closedAt(ticket.getClosedAt())
                .reopenedAt(ticket.getReopenedAt())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();

        if (includeReplies) {
            List<TicketReplyDTO> replies = ticketReplyRepository.findByTicketIdOrderByCreatedAtAsc(ticket.getId()).stream()
                    .map(r -> TicketReplyDTO.builder()
                            .id(r.getId())
                            .ticketId(r.getTicketId())
                            .senderType(r.getSenderType())
                            .senderId(r.getSenderId())
                            .senderName(r.getSenderName())
                            .senderEmail(r.getSenderEmail())
                            .message(r.getMessage())
                            .attachments(r.getAttachments())
                            .createdAt(r.getCreatedAt())
                            .build())
                    .collect(Collectors.toList());
            dto.setReplies(replies);
        }

        return dto;
    }
}
