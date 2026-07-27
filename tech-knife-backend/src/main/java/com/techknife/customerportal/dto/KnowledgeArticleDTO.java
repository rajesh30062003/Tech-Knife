package com.techknife.customerportal.dto;

import com.techknife.customerportal.entity.SupportTicket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeArticleDTO {

    private String id;
    private String slug;
    private String title;
    private String summary;
    private String content;
    private String categoryId;
    private String categoryName;
    private Long viewsCount;
    private Long helpfulCount;
    private Boolean isPublished;
    private Boolean isPopular;
    private String authorName;
    private List<String> tags;
    private List<SupportTicket.Attachment> attachments;
    private Instant createdAt;
    private Instant updatedAt;
}
