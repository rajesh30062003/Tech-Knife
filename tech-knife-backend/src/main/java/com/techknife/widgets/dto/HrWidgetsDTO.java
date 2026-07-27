package com.techknife.widgets.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HrWidgetsDTO {

    private Map<String, Integer> attendanceHeatmap; // Date -> Count
    private List<LeaveCalendarItem> leaveCalendar;
    private List<LateEmployeeItem> lateEmployees;
    private PendingApprovalsSummary pendingApprovals;
    private List<ExpiringCompOffItem> expiringCompOff;
    private List<ShiftSummaryItem> shiftSummary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeaveCalendarItem {
        private String employeeId;
        private String employeeName;
        private String leaveTypeName;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LateEmployeeItem {
        private String employeeId;
        private String employeeName;
        private String departmentId;
        private Integer lateMinutes;
        private String checkInTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingApprovalsSummary {
        private long pendingLeaves;
        private long pendingWfh;
        private long pendingRegularizations;
        private long pendingCompOffs;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExpiringCompOffItem {
        private String id;
        private String employeeId;
        private String employeeName;
        private Double days;
        private LocalDate expiryDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShiftSummaryItem {
        private String shiftId;
        private String shiftName;
        private long assignedEmployees;
        private long presentToday;
    }
}
