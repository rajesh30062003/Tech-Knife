package com.techknife.project.sprint.entity;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sprints")
public class Sprint {

    @Id
    private String id;

    @Indexed
    private String projectId;

    private String sprintName;

    private String sprintGoal;

    private LocalDate startDate;

    private LocalDate endDate;

    @Builder.Default
    private String status = "PLANNED"; // PLANNED, ACTIVE, COMPLETED, CLOSED

    @Builder.Default
    private Integer totalStoryPoints = 0;

    @Builder.Default
    private Integer completedStoryPoints = 0;

    @Builder.Default
    private List<String> taskIds = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
