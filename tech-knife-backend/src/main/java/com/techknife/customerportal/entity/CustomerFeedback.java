package com.techknife.customerportal.entity;

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
@Document(collection = "customer_feedbacks")
public class CustomerFeedback {

    @Id
    private String id;

    @Indexed
    private String customerAccountId;

    private String customerName;

    @Indexed
    private String projectId;

    private String projectName;

    private String employeeId;

    private String employeeName;

    private Integer projectRating; // 1 to 5

    private Integer employeeRating; // 1 to 5

    private String comments;

    private String suggestions;

    private Double satisfactionScore; // 0.0 to 10.0

    @CreatedDate
    private Instant createdAt;
}
