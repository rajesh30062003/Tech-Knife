package com.techknife.holiday.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

/**
 * MongoDB Document for Holidays (National, State, Company, Branch, Restricted, Floating).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "holidays")
public class Holiday {

    @Id
    private String id;

    private String name;

    private String description;

    @Indexed
    private LocalDate date;

    private Integer year;

    @Indexed
    private HolidayType type;

    private String branchId;

    private String state;

    @Builder.Default
    private Boolean restricted = false;

    @Builder.Default
    private Boolean active = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
