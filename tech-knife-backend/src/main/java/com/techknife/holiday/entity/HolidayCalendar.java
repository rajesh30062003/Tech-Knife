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
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB Document for Branch/Year Holiday Calendars.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "holiday_calendars")
@CompoundIndexes({
    @CompoundIndex(name = "year_branch_idx", def = "{'year': 1, 'branchId': 1}", unique = true)
})
public class HolidayCalendar {

    @Id
    private String id;

    private String name;

    private Integer year;

    private String branchId;

    private String branchName;

    @Builder.Default
    private List<String> holidayIds = new ArrayList<>();

    @Builder.Default
    private Integer maxRestrictedHolidaysAllowed = 2;

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
