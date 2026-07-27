package com.techknife.customerportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSearchDTO {

    private String query;
    private List<CustomerProjectDTO> projects;
    private List<SupportTicketDTO> tickets;
    private List<InvoiceViewDTO> invoices;
    private List<SharedDocumentDTO> documents;
    private List<KnowledgeArticleDTO> articles;
}
