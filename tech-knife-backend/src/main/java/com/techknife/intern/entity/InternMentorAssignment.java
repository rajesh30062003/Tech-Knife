package com.techknife.intern.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternMentorAssignment {
    private String id;
    private String mentorId;
    private String mentorName;
    private LocalDate assignedDate;
    private LocalDate unassignedDate;
    private String reason;
    private String assignedBy;
}
