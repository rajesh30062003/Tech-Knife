package com.techknife.leave.report;

import com.techknife.holiday.entity.Holiday;
import com.techknife.holiday.repository.HolidayRepository;
import com.techknife.leave.dto.LeaveReportDTO;
import com.techknife.leave.entity.LeaveRequest;
import com.techknife.leave.entity.WorkFromHomeRequest;
import com.techknife.leave.repository.LeaveRequestRepository;
import com.techknife.leave.repository.WorkFromHomeRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveReportService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final WorkFromHomeRequestRepository wfhRequestRepository;
    private final HolidayRepository holidayRepository;

    public List<LeaveReportDTO> getLeaveRegister(LocalDate startDate, LocalDate endDate, String departmentId) {
        List<LeaveRequest> requests = (startDate != null && endDate != null)
                ? leaveRequestRepository.findByStartDateBetweenOrEndDateBetween(startDate, endDate, startDate, endDate)
                : leaveRequestRepository.findAll();

        if (departmentId != null && !departmentId.isBlank()) {
            requests = requests.stream()
                    .filter(r -> departmentId.equals(r.getDepartmentId()))
                    .collect(Collectors.toList());
        }

        return requests.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<LeaveReportDTO> getWfhReport(LocalDate startDate, LocalDate endDate, String departmentId) {
        List<WorkFromHomeRequest> wfhList = (startDate != null && endDate != null)
                ? wfhRequestRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(startDate, endDate)
                : wfhRequestRepository.findAll();

        if (departmentId != null && !departmentId.isBlank()) {
            wfhList = wfhList.stream()
                    .filter(w -> departmentId.equals(w.getDepartmentId()))
                    .collect(Collectors.toList());
        }

        return wfhList.stream().map(w -> LeaveReportDTO.builder()
                .id(w.getId())
                .employeeId(w.getEmployeeId())
                .employeeName(w.getEmployeeName())
                .departmentId(w.getDepartmentId())
                .leaveTypeName("Work From Home")
                .startDate(w.getStartDate())
                .endDate(w.getEndDate())
                .totalDays(w.getTotalDays())
                .reason(w.getReason())
                .status(w.getStatus() != null ? w.getStatus().name() : null)
                .appliedAt(w.getCreatedAt() != null ? w.getCreatedAt().toString() : null)
                .build()).collect(Collectors.toList());
    }

    public List<Holiday> getHolidayReport(Integer year) {
        int reqYear = (year != null) ? year : LocalDate.now().getYear();
        return holidayRepository.findByYear(reqYear);
    }

    public String exportToCsv(List<LeaveReportDTO> reports) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Employee ID,Employee Name,Department ID,Type,Start Date,End Date,Total Days,Reason,Status,Applied At\n");
        for (LeaveReportDTO r : reports) {
            csv.append(String.format("%s,%s,\"%s\",%s,\"%s\",%s,%s,%.1f,\"%s\",%s,%s\n",
                    r.getId(),
                    r.getEmployeeId(),
                    r.getEmployeeName() != null ? r.getEmployeeName() : "",
                    r.getDepartmentId() != null ? r.getDepartmentId() : "",
                    r.getLeaveTypeName() != null ? r.getLeaveTypeName() : "",
                    r.getStartDate(),
                    r.getEndDate(),
                    r.getTotalDays() != null ? r.getTotalDays() : 0.0,
                    r.getReason() != null ? r.getReason() : "",
                    r.getStatus(),
                    r.getAppliedAt() != null ? r.getAppliedAt() : ""
            ));
        }
        return csv.toString();
    }

    private LeaveReportDTO mapToDTO(LeaveRequest request) {
        return LeaveReportDTO.builder()
                .id(request.getId())
                .employeeId(request.getEmployeeId())
                .employeeName(request.getEmployeeName())
                .departmentId(request.getDepartmentId())
                .leaveTypeName(request.getLeaveTypeName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalDays(request.getTotalDays())
                .reason(request.getReason())
                .status(request.getStatus() != null ? request.getStatus().name() : null)
                .appliedAt(request.getCreatedAt() != null ? request.getCreatedAt().toString() : null)
                .build();
    }
}
