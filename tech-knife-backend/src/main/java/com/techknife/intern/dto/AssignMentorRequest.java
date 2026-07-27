package com.techknife.intern.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignMentorRequest {

    @NotBlank(message = "Mentor employee ID is mandatory")
    private String mentorId;

    private LocalDate assignedDate;
    private Integer maxInternLimit;
    private String remarks;
}
