package com.techknife.project.sprint.dto;

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
public class SprintRetrospectiveDTO {

    private String id;
    private String sprintId;
    private List<String> whatWentWell;
    private List<String> whatCouldBeImproved;
    private List<String> actionItems;
    private Instant createdAt;
}
