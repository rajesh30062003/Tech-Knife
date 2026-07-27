package com.techknife.employee.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Experience {
    private String id;
    private String company;
    private String designation;
    private LocalDate startDate;
    private LocalDate endDate;
    private String responsibilities;
    private List<String> technologies;
}
