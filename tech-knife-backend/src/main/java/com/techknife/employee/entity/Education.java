package com.techknife.employee.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Education {
    private String id;
    private String degree;
    private String institute;
    private String university;
    private String board;
    private Integer passingYear;
    private Double percentage;
    private Double cgpa;
}
