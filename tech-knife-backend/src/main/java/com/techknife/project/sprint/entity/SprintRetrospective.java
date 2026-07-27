package com.techknife.project.sprint.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sprint_retrospectives")
public class SprintRetrospective {

    @Id
    private String id;

    private String sprintId;

    @Builder.Default
    private List<String> whatWentWell = new ArrayList<>();

    @Builder.Default
    private List<String> whatCouldBeImproved = new ArrayList<>();

    @Builder.Default
    private List<String> actionItems = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;
}
