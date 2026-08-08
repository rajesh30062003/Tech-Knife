package com.techknife.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "projectActivities")
public class ProjectActivity {

    @Id
    private String id;

    @Indexed
    private String projectId;

    @Indexed
    private String projectCode;

    private String action;          // e.g. "Task Completed", "Document Uploaded", "Repository Updated"

    private String activityType;    // e.g. "TASK", "DOCUMENT", "REPOSITORY", "STATUS", "MEETING", "TEAM", "PLANNING", "RISK"

    private String description;     // Human-readable summary details e.g. "Completed task 'Implement authentication API'"

    private String performedBy;     // User Full Name e.g. "Rahul Pal"

    private String performedByEmail; // User Email e.g. "rahul.pal@techknife.com"

    private String userRole;        // Role e.g. "Fullstack Developer", "Manager", "CTO"

    private String fieldModified;   // Changed property name e.g. "Backend Repository", "Status"

    private String oldValue;        // Pre-update value

    private String newValue;        // Post-update value

    @CreatedDate
    private Instant timestamp;
}
