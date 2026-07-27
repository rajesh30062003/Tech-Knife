package com.techknife.customerportal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "knowledge_articles")
public class KnowledgeBase {

    @Id
    private String id;

    @Indexed(unique = true)
    private String slug;

    private String title;

    private String summary;

    private String content;

    @Indexed
    private String categoryId;

    private String categoryName;

    @Builder.Default
    private Long viewsCount = 0L;

    @Builder.Default
    private Long helpfulCount = 0L;

    @Builder.Default
    private Boolean isPublished = true;

    @Builder.Default
    private Boolean isPopular = false;

    private String authorName;

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    private List<SupportTicket.Attachment> attachments = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
