package com.techknife.timetracking.service;

import com.techknife.timetracking.dto.TimeEntryDTO;
import com.techknife.timetracking.dto.TimesheetApprovalRequest;
import com.techknife.timetracking.dto.TimesheetDTO;
import com.techknife.timetracking.entity.TimeEntry;
import com.techknife.timetracking.entity.Timesheet;
import com.techknife.timetracking.repository.TimeEntryRepository;
import com.techknife.timetracking.repository.TimesheetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimesheetService {

    private final TimesheetRepository timesheetRepository;
    private final TimeEntryRepository timeEntryRepository;
    private final TimeTrackingService timeTrackingService;

    public TimesheetDTO generateOrSubmitTimesheet(String employeeId, String periodType, LocalDate startDate, LocalDate endDate) {
        Instant startInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<TimeEntry> entries = timeEntryRepository.findByEmployeeIdAndStartTimeBetween(employeeId, startInstant, endInstant);

        double totalMinutes = entries.stream().mapToLong(e -> e.getDurationInMinutes() != null ? e.getDurationInMinutes() : 0L).sum();
        double billableMinutes = entries.stream().filter(TimeEntry::isBillable)
                .mapToLong(e -> e.getDurationInMinutes() != null ? e.getDurationInMinutes() : 0L).sum();

        double totalHours = totalMinutes / 60.0;
        double billableHours = billableMinutes / 60.0;
        double nonBillableHours = Math.max(0, totalHours - billableHours);

        List<String> entryIds = entries.stream().map(TimeEntry::getId).collect(Collectors.toList());

        Timesheet timesheet = timesheetRepository.findByEmployeeIdAndPeriodStartDateAndPeriodEndDate(employeeId, startDate, endDate)
                .orElseGet(() -> Timesheet.builder()
                        .employeeId(employeeId)
                        .periodType(periodType)
                        .periodStartDate(startDate)
                        .periodEndDate(endDate)
                        .build());

        timesheet.setTotalHours(totalHours);
        timesheet.setBillableHours(billableHours);
        timesheet.setNonBillableHours(nonBillableHours);
        timesheet.setTimeEntryIds(entryIds);
        timesheet.setStatus("SUBMITTED");
        timesheet.setSubmittedAt(Instant.now());

        Timesheet saved = timesheetRepository.save(timesheet);
        return mapToDTO(saved);
    }

    public TimesheetDTO approveTimesheet(String timesheetId, TimesheetApprovalRequest request) {
        Timesheet timesheet = timesheetRepository.findById(timesheetId)
                .orElseThrow(() -> new NoSuchElementException("Timesheet not found: " + timesheetId));

        timesheet.setStatus("APPROVED");
        timesheet.setApproverId(request.getApproverId());
        timesheet.setApprovedAt(Instant.now());

        Timesheet saved = timesheetRepository.save(timesheet);
        return mapToDTO(saved);
    }

    public TimesheetDTO rejectTimesheet(String timesheetId, TimesheetApprovalRequest request) {
        Timesheet timesheet = timesheetRepository.findById(timesheetId)
                .orElseThrow(() -> new NoSuchElementException("Timesheet not found: " + timesheetId));

        timesheet.setStatus("REJECTED");
        timesheet.setApproverId(request.getApproverId());
        timesheet.setRejectionReason(request.getRejectionReason());

        Timesheet saved = timesheetRepository.save(timesheet);
        return mapToDTO(saved);
    }

    public List<TimesheetDTO> getTimesheetsByEmployee(String employeeId) {
        return timesheetRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<TimesheetDTO> getPendingTimesheets() {
        return timesheetRepository.findByStatus("SUBMITTED").stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TimesheetDTO getTimesheetById(String timesheetId) {
        Timesheet timesheet = timesheetRepository.findById(timesheetId)
                .orElseThrow(() -> new NoSuchElementException("Timesheet not found: " + timesheetId));
        return mapToDTO(timesheet);
    }

    private TimesheetDTO mapToDTO(Timesheet t) {
        List<TimeEntryDTO> entries = t.getTimeEntryIds() != null
                ? timeEntryRepository.findAllById(t.getTimeEntryIds()).stream().map(timeTrackingService::mapToDTO).collect(Collectors.toList())
                : List.of();

        return TimesheetDTO.builder()
                .id(t.getId())
                .employeeId(t.getEmployeeId())
                .periodType(t.getPeriodType())
                .periodStartDate(t.getPeriodStartDate())
                .periodEndDate(t.getPeriodEndDate())
                .totalHours(t.getTotalHours())
                .billableHours(t.getBillableHours())
                .nonBillableHours(t.getNonBillableHours())
                .status(t.getStatus())
                .approverId(t.getApproverId())
                .rejectionReason(t.getRejectionReason())
                .timeEntryIds(t.getTimeEntryIds())
                .timeEntries(entries)
                .submittedAt(t.getSubmittedAt())
                .approvedAt(t.getApprovedAt())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}
