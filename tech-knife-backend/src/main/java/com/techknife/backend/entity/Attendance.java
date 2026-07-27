package com.techknife.backend.entity;

import com.techknife.backend.constant.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "attendances")
@CompoundIndexes({
    @CompoundIndex(name = "user_date_idx", def = "{'userId': 1, 'date': 1}", unique = true)
})
public class Attendance extends BaseEntity {

    @Indexed
    private String userId;

    private String userEmail;
    private String userName;
    private String department;

    @Indexed
    private LocalDate date;

    private AttendanceStatus status;

    private Instant checkInTime;
    private Instant checkOutTime;

    private long totalWorkMinutes;
    private long totalBreakMinutes;
    private long overtimeMinutes;

    private boolean isLateArrival;
    private boolean isEarlyLeaving;
    private boolean isHalfDay;
    private boolean isWorkFromHome;
    private boolean isHoliday;
    private boolean isWeekend;

    private String remarks;
    private String locationIn;
    private String locationOut;
    private String ipAddress;

    private List<PunchLog> punches = new ArrayList<>();

    private boolean correctedByAdmin;
    private String correctionReason;
}
