package com.techknife.employee.service.impl;

import com.techknife.backend.event.EmployeeCreatedEvent;
import com.techknife.backend.exception.BadRequestException;
import com.techknife.employee.dto.BulkDepartmentTransferRequest;
import com.techknife.employee.dto.BulkOperationResponse;
import com.techknife.employee.dto.BulkRowError;
import com.techknife.employee.dto.BulkStatusChangeRequest;
import com.techknife.employee.entity.Employee;
import com.techknife.employee.entity.EmployeeStatus;
import com.techknife.employee.entity.Gender;
import com.techknife.employee.entity.TimelineEventType;
import com.techknife.employee.repository.EmployeeRepository;
import com.techknife.employee.service.EmployeeBulkService;
import com.techknife.employee.service.EmployeeTimelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeBulkServiceImpl implements EmployeeBulkService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeTimelineService timelineService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public BulkOperationResponse bulkImport(MultipartFile file, String importedBy) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file is empty");
        }

        List<BulkRowError> errors = new ArrayList<>();
        int totalRows = 0;
        int successCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine(); // Skip header line
            if (headerLine == null) {
                throw new BadRequestException("File is empty or missing headers");
            }

            String line;
            int rowNum = 1;
            while ((line = reader.readLine()) != null) {
                rowNum++;
                if (line.trim().isEmpty()) continue;

                totalRows++;
                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (tokens.length < 5) {
                    errors.add(BulkRowError.builder()
                            .rowNumber(rowNum)
                            .identifier("Row " + rowNum)
                            .errorMessage("Invalid column count. Required at least 5 columns (Code, Email, FirstName, LastName, PrimaryMobile)")
                            .build());
                    continue;
                }

                String empCode = clean(tokens[0]);
                String email = clean(tokens[1]);
                String firstName = clean(tokens[2]);
                String lastName = clean(tokens[3]);
                String mobile = clean(tokens[4]);

                if (empCode.isEmpty() || email.isEmpty() || firstName.isEmpty()) {
                    errors.add(BulkRowError.builder()
                            .rowNumber(rowNum)
                            .identifier(empCode)
                            .errorMessage("Mandatory fields missing (Employee Code, Email, or First Name)")
                            .build());
                    continue;
                }

                if (employeeRepository.existsByEmployeeId(empCode)) {
                    errors.add(BulkRowError.builder()
                            .rowNumber(rowNum)
                            .identifier(empCode)
                            .errorMessage("Employee code '" + empCode + "' already exists in database")
                            .build());
                    continue;
                }

                if (employeeRepository.existsByOfficialEmail(email)) {
                    errors.add(BulkRowError.builder()
                            .rowNumber(rowNum)
                            .identifier(email)
                            .errorMessage("Official email '" + email + "' already exists in database")
                            .build());
                    continue;
                }

                String companyId = tokens.length > 5 ? clean(tokens[5]) : "";
                String branchId = tokens.length > 6 ? clean(tokens[6]) : "";
                String deptId = tokens.length > 7 ? clean(tokens[7]) : "";
                String designationId = tokens.length > 8 ? clean(tokens[8]) : "";

                Employee employee = Employee.builder()
                        .employeeId(empCode)
                        .officialEmail(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .primaryMobile(mobile)
                        .companyId(companyId)
                        .branchId(branchId)
                        .departmentId(deptId)
                        .designationId(designationId)
                        .status(EmployeeStatus.ACTIVE)
                        .joiningDate(LocalDate.now())
                        .createdBy(importedBy)
                        .createdAt(Instant.now())
                        .build();

                Employee saved = employeeRepository.save(employee);
                successCount++;

                timelineService.recordTimelineEvent(saved.getId(), TimelineEventType.ONBOARDED, null, "ACTIVE", "Bulk Onboarded", importedBy);
                eventPublisher.publishEvent(new EmployeeCreatedEvent(this, saved.getId(), saved.getEmployeeId(), saved.getOfficialEmail(), firstName + " " + lastName));
            }
        } catch (Exception e) {
            log.error("Error processing bulk import file", e);
            throw new BadRequestException("Failed to process bulk import CSV: " + e.getMessage());
        }

        return BulkOperationResponse.builder()
                .totalRecordsProcessed(totalRows)
                .successCount(successCount)
                .failureCount(errors.size())
                .errors(errors)
                .message("Bulk import completed. Successfully imported " + successCount + " out of " + totalRows + " records.")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] bulkExportCSV(String departmentId, EmployeeStatus status) {
        List<Employee> employees;
        if (departmentId != null && !departmentId.trim().isEmpty()) {
            employees = employeeRepository.findByDepartmentId(departmentId);
        } else if (status != null) {
            employees = employeeRepository.findByStatus(status);
        } else {
            employees = employeeRepository.findAll();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("ID,EmployeeCode,OfficialEmail,FirstName,LastName,PrimaryMobile,CompanyID,BranchID,DepartmentID,DesignationID,Status,JoiningDate\n");

        for (Employee e : employees) {
            sb.append(e.getId()).append(",")
                    .append(escapeCsv(e.getEmployeeId())).append(",")
                    .append(escapeCsv(e.getOfficialEmail())).append(",")
                    .append(escapeCsv(e.getFirstName())).append(",")
                    .append(escapeCsv(e.getLastName())).append(",")
                    .append(escapeCsv(e.getPrimaryMobile())).append(",")
                    .append(escapeCsv(e.getCompanyId())).append(",")
                    .append(escapeCsv(e.getBranchId())).append(",")
                    .append(escapeCsv(e.getDepartmentId())).append(",")
                    .append(escapeCsv(e.getDesignationId())).append(",")
                    .append(e.getStatus() != null ? e.getStatus().name() : "").append(",")
                    .append(e.getJoiningDate() != null ? e.getJoiningDate().toString() : "")
                    .append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    @Transactional
    public BulkOperationResponse bulkStatusChange(BulkStatusChangeRequest request, String updatedBy) {
        List<BulkRowError> errors = new ArrayList<>();
        int successCount = 0;

        for (int i = 0; i < request.getEmployeeIds().size(); i++) {
            String empId = request.getEmployeeIds().get(i);
            try {
                Employee employee = employeeRepository.findById(empId)
                        .orElseThrow(() -> new BadRequestException("Employee ID not found: " + empId));

                String oldStatus = employee.getStatus() != null ? employee.getStatus().name() : "NONE";
                employee.setStatus(request.getStatus());
                employeeRepository.save(employee);

                timelineService.recordTimelineEvent(empId, TimelineEventType.STATUS_CHANGE, oldStatus, request.getStatus().name(), request.getRemarks(), updatedBy);
                successCount++;
            } catch (Exception e) {
                errors.add(BulkRowError.builder()
                        .rowNumber(i + 1)
                        .identifier(empId)
                        .errorMessage(e.getMessage())
                        .build());
            }
        }

        return BulkOperationResponse.builder()
                .totalRecordsProcessed(request.getEmployeeIds().size())
                .successCount(successCount)
                .failureCount(errors.size())
                .errors(errors)
                .message("Bulk status change completed.")
                .build();
    }

    @Override
    @Transactional
    public BulkOperationResponse bulkDepartmentTransfer(BulkDepartmentTransferRequest request, String updatedBy) {
        List<BulkRowError> errors = new ArrayList<>();
        int successCount = 0;

        for (int i = 0; i < request.getEmployeeIds().size(); i++) {
            String empId = request.getEmployeeIds().get(i);
            try {
                Employee employee = employeeRepository.findById(empId)
                        .orElseThrow(() -> new BadRequestException("Employee ID not found: " + empId));

                String oldDept = employee.getDepartmentId();
                employee.setDepartmentId(request.getTargetDepartmentId());
                employeeRepository.save(employee);

                timelineService.recordTimelineEvent(empId, TimelineEventType.DEPARTMENT_CHANGE, oldDept, request.getTargetDepartmentId(), request.getRemarks(), updatedBy);
                successCount++;
            } catch (Exception e) {
                errors.add(BulkRowError.builder()
                        .rowNumber(i + 1)
                        .identifier(empId)
                        .errorMessage(e.getMessage())
                        .build());
            }
        }

        return BulkOperationResponse.builder()
                .totalRecordsProcessed(request.getEmployeeIds().size())
                .successCount(successCount)
                .failureCount(errors.size())
                .errors(errors)
                .message("Bulk department transfer completed.")
                .build();
    }

    private String clean(String val) {
        if (val == null) return "";
        return val.trim().replaceAll("^\"|\"$", "");
    }

    private String escapeCsv(String val) {
        if (val == null) return "";
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }
}
