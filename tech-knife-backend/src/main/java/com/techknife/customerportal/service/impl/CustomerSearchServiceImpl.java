package com.techknife.customerportal.service.impl;

import com.techknife.customerportal.dto.*;
import com.techknife.customerportal.repository.*;
import com.techknife.customerportal.service.CustomerSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerSearchServiceImpl implements CustomerSearchService {

    private final CustomerProjectRepository customerProjectRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final InvoiceViewRepository invoiceViewRepository;
    private final SharedDocumentRepository sharedDocumentRepository;
    private final KnowledgeBaseRepository knowledgeBaseRepository;

    @Override
    public CustomerSearchDTO search(String customerAccountId, String query) {
        if (query == null || query.trim().length() < 2) {
            return CustomerSearchDTO.builder()
                    .query(query)
                    .projects(List.of())
                    .tickets(List.of())
                    .invoices(List.of())
                    .documents(List.of())
                    .articles(List.of())
                    .build();

        }

        String q = query.trim();

        List<CustomerProjectDTO> projects = customerProjectRepository.findByCustomerAccountId(customerAccountId).stream()
                .filter(p -> (p.getProjectName() != null && p.getProjectName().toLowerCase().contains(q.toLowerCase())) ||
                        (p.getDescription() != null && p.getDescription().toLowerCase().contains(q.toLowerCase())) ||
                        (p.getProjectCode() != null && p.getProjectCode().toLowerCase().contains(q.toLowerCase())))
                .map(p -> CustomerProjectDTO.builder()
                        .id(p.getId())
                        .projectCode(p.getProjectCode())
                        .projectName(p.getProjectName())
                        .description(p.getDescription())
                        .status(p.getStatus())
                        .build())
                .collect(Collectors.toList());

        List<SupportTicketDTO> tickets = supportTicketRepository.findByCustomerAccountId(customerAccountId).stream()
                .filter(t -> (t.getTitle() != null && t.getTitle().toLowerCase().contains(q.toLowerCase())) ||
                        (t.getDescription() != null && t.getDescription().toLowerCase().contains(q.toLowerCase())) ||
                        (t.getTicketNumber() != null && t.getTicketNumber().toLowerCase().contains(q.toLowerCase())))
                .map(t -> SupportTicketDTO.builder()
                        .id(t.getId())
                        .ticketNumber(t.getTicketNumber())
                        .title(t.getTitle())
                        .status(t.getStatus())
                        .priority(t.getPriority())
                        .build())
                .collect(Collectors.toList());

        List<InvoiceViewDTO> invoices = invoiceViewRepository.findByCustomerAccountIdAndInvoiceNumberContainingIgnoreCase(customerAccountId, q).stream()
                .map(i -> InvoiceViewDTO.builder()
                        .id(i.getId())
                        .invoiceNumber(i.getInvoiceNumber())
                        .totalAmount(i.getTotalAmount())
                        .status(i.getStatus())
                        .issueDate(i.getIssueDate())
                        .dueDate(i.getDueDate())
                        .build())
                .collect(Collectors.toList());

        List<SharedDocumentDTO> docs = sharedDocumentRepository.findByCustomerAccountIdAndDocumentNameContainingIgnoreCase(customerAccountId, q).stream()
                .map(d -> SharedDocumentDTO.builder()
                        .id(d.getId())
                        .documentName(d.getDocumentName())
                        .category(d.getCategory())
                        .fileUrl(d.getFileUrl())
                        .fileType(d.getFileType())
                        .build())
                .collect(Collectors.toList());

        List<KnowledgeArticleDTO> articles = knowledgeBaseRepository.findByTitleContainingIgnoreCaseOrSummaryContainingIgnoreCaseOrContentContainingIgnoreCase(q, q, q).stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsPublished()))
                .map(a -> KnowledgeArticleDTO.builder()
                        .id(a.getId())
                        .slug(a.getSlug())
                        .title(a.getTitle())
                        .summary(a.getSummary())
                        .categoryName(a.getCategoryName())
                        .build())
                .collect(Collectors.toList());

        return CustomerSearchDTO.builder()
                .query(q)
                .projects(projects)
                .tickets(tickets)
                .invoices(invoices)
                .documents(docs)
                .articles(articles)
                .build();
    }
}
