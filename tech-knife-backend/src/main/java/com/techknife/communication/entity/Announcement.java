package com.techknife.communication.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "communication_announcements")
public class Announcement {

    @Id
    private String id;

    private String title;
    private String content;
    private String categoryId;
    private String categoryName;
    private String status; // DRAFT, PUBLISHED, ARCHIVED
    private String priority; // LOW, MEDIUM, HIGH, URGENT
    private String targetType; // ALL, DEPARTMENT, ROLE, SPECIFIC_USERS
    private List<String> targetValues;
    private String authorId;
    private String authorName;
    private Instant publishedAt;
    private Instant expiresAt;
    private long readCount;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;
}
