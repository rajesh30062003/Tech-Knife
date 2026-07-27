package com.techknife.recruitment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruitmentPipelineDTO {

    private String jobPostingId;

    private String jobTitle;

    private String department;

    private Long totalApplications;

    private Map<String, Long> stageCounts;

    private Map<String, List<ApplicationDTO>> applicationsByStage;
}
