package com.techknife.employee.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Skill {
    private String id;
    private String skillName;
    private SkillLevel level;
    private Double years;
    private String certification;
}
