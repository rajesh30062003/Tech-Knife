package com.techknife.leave.dto;

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
public class LeaveDashboardDTO {

    private long pendingLeaveRequests;
    private long approvedToday;
    private long rejectedToday;
    private long upcomingLeavesCount;

    private List<LeaveSummaryItem> pendingRequests;
    private List<LeaveSummaryItem> approvedTodayList;
    private List<LeaveSummaryItem> upcomingLeaves;
    private List<LeaveBalanceItem> leaveBalanceSummary;
    private Map<String, Long> departmentLeaveStatistics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeaveSummaryItem {
        private String id;
        private String employeeId;
        private String employeeName;
        private String leaveTypeName;
        private LocalDate startDate;
        private LocalDate endDate;
        private Double totalDays;
        private String reason;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeaveBalanceItem {
        private String leaveTypeName;
        private Double totalAllocated;
        private Double used;
        private Double remaining;
    }
}
