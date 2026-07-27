package com.techknife.attendance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comp_off_balances")
public class CompOffBalance {

    @Id
    private String id;

    @Indexed(unique = true)
    private String employeeId;

    @Builder.Default
    private Double availableDays = 0.0;

    @Builder.Default
    private Double usedDays = 0.0;

    @Builder.Default
    private Double expiredDays = 0.0;

    @LastModifiedDate
    private Instant updatedAt;
}
