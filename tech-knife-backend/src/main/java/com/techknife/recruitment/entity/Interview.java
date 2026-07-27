package com.techknife.recruitment.entity;

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
@Document(collection = "interviews")
public class Interview {

    @Id
    private String id;

    @Indexed
    private String applicationId;

    @Indexed
    private String candidateId;

    @Indexed
    private String jobPostingId;

    private String interviewType; // TECHNICAL, HR, MANAGERIAL

    private String mode; // ONLINE, OFFLINE

    @Builder.Default
    private List<String> panelMembers = new ArrayList<>();

    private Instant interviewTime;

    private String locationOrLink;

    @Builder.Default
    private String result = "SCHEDULED"; // SCHEDULED, PASSED, FAILED, CANCELLED, PENDING_FEEDBACK

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
