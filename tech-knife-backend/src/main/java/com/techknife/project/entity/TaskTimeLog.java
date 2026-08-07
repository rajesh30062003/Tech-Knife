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
@Document(collection = "task_time_logs")
public class TaskTimeLog {

    @Id
    private String id;

    @Indexed
    private String taskId;

    @Indexed
    private String projectId;

    private String userName;

    private String userRole;

    private Double hoursSpent;

    private String description;

    @CreatedDate
    private Instant logDate;
}
