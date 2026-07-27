package com.techknife.report.service.impl;

import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.report.dto.*;
import com.techknife.report.entity.*;
import com.techknife.report.repository.DashboardWidgetRepository;
import com.techknife.report.repository.ReportRepository;
import com.techknife.report.repository.WidgetLayoutRepository;
import com.techknife.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final DashboardWidgetRepository widgetRepository;
    private final WidgetLayoutRepository layoutRepository;

    @Override
    public ReportDTO createReport(ReportDTO dto) {
        validateDateRange(dto.getStartDate(), dto.getEndDate());

        Report report = mapToEntity(dto);
        Report saved = reportRepository.save(report);
        return mapToDTO(saved);
    }

    @Override
    public ReportDTO updateReport(String id, ReportDTO dto) {
        Report existing = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report", "id", id));

        validateDateRange(dto.getStartDate(), dto.getEndDate());

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setCategory(dto.getCategory());
        existing.setTemplateId(dto.getTemplateId());
        existing.setSelectedColumns(dto.getSelectedColumns());
        existing.setFilters(dto.getFilters());
        
        if (dto.getSorting() != null) {
            existing.setSorting(dto.getSorting().stream()
                    .map(s -> Report.SortConfig.builder().field(s.getField()).direction(s.getDirection()).build())
                    .collect(Collectors.toList()));
        }
        existing.setGrouping(dto.getGrouping());
        if (dto.getAggregations() != null) {
            existing.setAggregations(dto.getAggregations().stream()
                    .map(a -> Report.AggregationConfig.builder().field(a.getField()).function(a.getFunction()).alias(a.getAlias()).build())
                    .collect(Collectors.toList()));
        }
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setPageNumber(dto.getPageNumber());
        existing.setPageSize(dto.getPageSize());
        existing.setSaved(dto.isSaved());
        existing.setTemplate(dto.isTemplate());
        existing.setTags(dto.getTags());

        Report updated = reportRepository.save(existing);
        return mapToDTO(updated);
    }

    @Override
    public ReportDTO getReportById(String id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report", "id", id));
        return mapToDTO(report);
    }

    @Override
    public List<ReportDTO> getAllReports() {
        return reportRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportDTO> getSavedReports() {
        return reportRepository.findBySavedTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportDTO> getReportsByCategory(ReportCategoryType category) {
        return reportRepository.findByCategory(category).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteReport(String id) {
        if (!reportRepository.existsById(id)) {
            throw new ResourceNotFoundException("Report", "id", id);
        }
        reportRepository.deleteById(id);
    }

    @Override
    public Map<String, Object> executeReport(ReportBuildRequest request) {
        validateDateRange(request.getStartDate(), request.getEndDate());

        Map<String, Object> response = new HashMap<>();
        response.put("reportName", request.getName() != null ? request.getName() : "Custom Dynamic Report");
        response.put("category", request.getCategory());
        response.put("executedAt", Instant.now());
        response.put("selectedColumns", request.getSelectedColumns() != null ? request.getSelectedColumns() : Collections.emptyList());
        response.put("appliedFilters", request.getFilters() != null ? request.getFilters() : Collections.emptyMap());
        response.put("grouping", request.getGrouping() != null ? request.getGrouping() : Collections.emptyList());
        response.put("pageNumber", request.getPageNumber());
        response.put("pageSize", request.getPageSize());

        // Simulated dynamic data generation based on selected columns and aggregations
        List<Map<String, Object>> records = new ArrayList<>();
        List<String> columns = request.getSelectedColumns() != null && !request.getSelectedColumns().isEmpty()
                ? request.getSelectedColumns()
                : List.of("id", "name", "category", "status", "createdDate", "amount");

        for (int i = 1; i <= Math.min(request.getPageSize(), 20); i++) {
            Map<String, Object> record = new LinkedHashMap<>();
            for (String col : columns) {
                switch (col.toLowerCase()) {
                    case "id":
                        record.put(col, "REC-" + (1000 + i));
                        break;
                    case "name":
                        record.put(col, "Report Item #" + i);
                        break;
                    case "category":
                        record.put(col, request.getCategory() != null ? request.getCategory().name() : "GENERAL");
                        break;
                    case "status":
                        record.put(col, i % 2 == 0 ? "ACTIVE" : "COMPLETED");
                        break;
                    case "amount":
                        record.put(col, 500.0 * i);
                        break;
                    default:
                        record.put(col, "Value_" + col + "_" + i);
                        break;
                }
            }
            records.add(record);
        }

        response.put("totalRecords", 100);
        response.put("totalPages", (int) Math.ceil(100.0 / Math.max(request.getPageSize(), 1)));
        response.put("data", records);

        if (request.isSaveReport() && request.getName() != null) {
            Report report = Report.builder()
                    .name(request.getName())
                    .category(request.getCategory())
                    .templateId(request.getTemplateId())
                    .selectedColumns(request.getSelectedColumns())
                    .filters(request.getFilters())
                    .grouping(request.getGrouping())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .pageNumber(request.getPageNumber())
                    .pageSize(request.getPageSize())
                    .saved(true)
                    .build();
            reportRepository.save(report);
        }

        return response;
    }

    @Override
    public KpiReportDTO generateKpiReport(String metricKey) {
        Instant now = Instant.now();
        KpiReportDTO.KpiReportDTOBuilder builder = KpiReportDTO.builder()
                .metricKey(metricKey)
                .calculatedAt(now);

        switch (metricKey.toUpperCase()) {
            case "EMPLOYEE_COUNT":
                builder.title("Employee Count")
                       .currentValue(250L)
                       .previousValue(235L)
                       .percentageChange(6.38)
                       .unit("Employees")
                       .employeeCount(250L);
                break;
            case "ATTENDANCE_PCT":
                builder.title("Attendance Rate")
                       .currentValue(94.5)
                       .previousValue(92.1)
                       .percentageChange(2.61)
                       .unit("%")
                       .attendancePercentage(94.5);
                break;
            case "LEAVE_UTILIZATION":
                builder.title("Leave Utilization")
                       .currentValue(18.2)
                       .previousValue(15.0)
                       .percentageChange(21.33)
                       .unit("%")
                       .leaveUtilizationRate(18.2);
                break;
            case "PAYROLL_COST":
                builder.title("Payroll Total Cost")
                       .currentValue(new BigDecimal("450000.00"))
                       .previousValue(new BigDecimal("430000.00"))
                       .percentageChange(4.65)
                       .unit("USD")
                       .payrollTotalCost(new BigDecimal("450000.00"));
                break;
            case "RECRUITMENT_FUNNEL":
                Map<String, Long> funnel = new LinkedHashMap<>();
                funnel.put("APPLIED", 120L);
                funnel.put("SCREENED", 60L);
                funnel.put("INTERVIEWED", 25L);
                funnel.put("OFFERED", 8L);
                funnel.put("HIRED", 5L);
                builder.title("Recruitment Funnel")
                       .currentValue(5L)
                       .previousValue(3L)
                       .percentageChange(66.67)
                       .unit("Hires")
                       .recruitmentFunnel(funnel);
                break;
            case "LEAD_CONVERSION":
                builder.title("Lead Conversion Rate")
                       .currentValue(12.8)
                       .previousValue(10.5)
                       .percentageChange(21.9)
                       .unit("%")
                       .leadConversionRate(12.8);
                break;
            case "PROJECT_COMPLETION":
                builder.title("Project Completion Rate")
                       .currentValue(88.4)
                       .previousValue(82.0)
                       .percentageChange(7.8)
                       .unit("%")
                       .projectCompletionPercentage(88.4);
                break;
            case "REVENUE":
                builder.title("Total Revenue")
                       .currentValue(new BigDecimal("1250000.00"))
                       .previousValue(new BigDecimal("1100000.00"))
                       .percentageChange(13.64)
                       .unit("USD")
                       .revenue(new BigDecimal("1250000.00"));
                break;
            case "EXPENSES":
                builder.title("Total Expenses")
                       .currentValue(new BigDecimal("780000.00"))
                       .previousValue(new BigDecimal("720000.00"))
                       .percentageChange(8.33)
                       .unit("USD")
                       .expenses(new BigDecimal("780000.00"));
                break;
            case "PROFIT":
                builder.title("Net Profit")
                       .currentValue(new BigDecimal("470000.00"))
                       .previousValue(new BigDecimal("380000.00"))
                       .percentageChange(23.68)
                       .unit("USD")
                       .profit(new BigDecimal("470000.00"));
                break;
            case "INVENTORY_VALUE":
                builder.title("Total Inventory Value")
                       .currentValue(new BigDecimal("320000.00"))
                       .previousValue(new BigDecimal("310000.00"))
                       .percentageChange(3.23)
                       .unit("USD")
                       .inventoryValue(new BigDecimal("320000.00"));
                break;
            case "ASSET_UTILIZATION":
                builder.title("Asset Utilization Rate")
                       .currentValue(82.5)
                       .previousValue(79.0)
                       .percentageChange(4.43)
                       .unit("%")
                       .assetUtilizationPercentage(82.5);
                break;
            default:
                builder.title(metricKey)
                       .currentValue(100)
                       .previousValue(90)
                       .percentageChange(11.11)
                       .unit("Units");
                break;
        }

        return builder.build();
    }

    @Override
    public List<KpiReportDTO> getAllKpiReports() {
        List<String> keys = List.of(
                "EMPLOYEE_COUNT", "ATTENDANCE_PCT", "LEAVE_UTILIZATION",
                "PAYROLL_COST", "RECRUITMENT_FUNNEL", "LEAD_CONVERSION",
                "PROJECT_COMPLETION", "REVENUE", "EXPENSES", "PROFIT",
                "INVENTORY_VALUE", "ASSET_UTILIZATION"
        );
        return keys.stream()
                .map(this::generateKpiReport)
                .collect(Collectors.toList());
    }

    @Override
    public ExecutiveDashboardDTO generateExecutiveDashboard(DashboardType dashboardType) {
        Instant now = Instant.now();
        List<KpiReportDTO> kpis = new ArrayList<>();
        Map<String, Object> summary = new HashMap<>();

        switch (dashboardType) {
            case CEO_DASHBOARD:
                kpis.add(generateKpiReport("REVENUE"));
                kpis.add(generateKpiReport("PROFIT"));
                kpis.add(generateKpiReport("EMPLOYEE_COUNT"));
                kpis.add(generateKpiReport("PROJECT_COMPLETION"));
                summary.put("status", "HEALTHY");
                summary.put("quarterGoalProgress", "85%");
                break;
            case HR_DASHBOARD:
                kpis.add(generateKpiReport("EMPLOYEE_COUNT"));
                kpis.add(generateKpiReport("ATTENDANCE_PCT"));
                kpis.add(generateKpiReport("LEAVE_UTILIZATION"));
                kpis.add(generateKpiReport("RECRUITMENT_FUNNEL"));
                summary.put("openPositions", 12);
                summary.put("turnoverRate", "2.1%");
                break;
            case FINANCE_DASHBOARD:
                kpis.add(generateKpiReport("REVENUE"));
                kpis.add(generateKpiReport("EXPENSES"));
                kpis.add(generateKpiReport("PROFIT"));
                kpis.add(generateKpiReport("PAYROLL_COST"));
                summary.put("cashReserve", "2.5M USD");
                summary.put("budgetUtilization", "74%");
                break;
            case SALES_DASHBOARD:
                kpis.add(generateKpiReport("REVENUE"));
                kpis.add(generateKpiReport("LEAD_CONVERSION"));
                summary.put("activePipeline", "4.2M USD");
                summary.put("wonDealsThisMonth", 18);
                break;
            case PROJECT_DASHBOARD:
                kpis.add(generateKpiReport("PROJECT_COMPLETION"));
                summary.put("activeProjects", 14);
                summary.put("delayedProjects", 2);
                break;
            case OPERATIONS_DASHBOARD:
                kpis.add(generateKpiReport("INVENTORY_VALUE"));
                kpis.add(generateKpiReport("ASSET_UTILIZATION"));
                summary.put("warehouseCapacityUsed", "68%");
                summary.put("pendingProcurementOrders", 5);
                break;
            default:
                kpis.addAll(getAllKpiReports());
                summary.put("totalMetrics", kpis.size());
                break;
        }

        return ExecutiveDashboardDTO.builder()
                .dashboardType(dashboardType)
                .title(dashboardType.name().replace("_", " "))
                .description("Executive dashboard overview for " + dashboardType.name())
                .generatedAt(now)
                .kpis(kpis)
                .summaryMetrics(summary)
                .build();
    }

    @Override
    public List<ReportDTO> searchReports(ReportSearchRequest searchRequest) {
        if (searchRequest.getQuery() != null && !searchRequest.getQuery().trim().isEmpty()) {
            return reportRepository.findByNameContainingIgnoreCase(searchRequest.getQuery().trim()).stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        }
        if (searchRequest.getCategory() != null) {
            return getReportsByCategory(searchRequest.getCategory());
        }
        if (Boolean.TRUE.equals(searchRequest.getSavedOnly())) {
            return getSavedReports();
        }
        return getAllReports();
    }

    private void validateDateRange(Instant startDate, Instant endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BadRequestException("Invalid Date Range: Start date (" + startDate + ") cannot be after end date (" + endDate + ")");
        }
    }

    private Report mapToEntity(ReportDTO dto) {
        List<Report.SortConfig> sorting = dto.getSorting() != null ? dto.getSorting().stream()
                .map(s -> Report.SortConfig.builder().field(s.getField()).direction(s.getDirection()).build())
                .collect(Collectors.toList()) : Collections.emptyList();

        List<Report.AggregationConfig> aggregations = dto.getAggregations() != null ? dto.getAggregations().stream()
                .map(a -> Report.AggregationConfig.builder().field(a.getField()).function(a.getFunction()).alias(a.getAlias()).build())
                .collect(Collectors.toList()) : Collections.emptyList();

        return Report.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .templateId(dto.getTemplateId())
                .selectedColumns(dto.getSelectedColumns())
                .filters(dto.getFilters())
                .sorting(sorting)
                .grouping(dto.getGrouping())
                .aggregations(aggregations)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .pageNumber(dto.getPageNumber())
                .pageSize(dto.getPageSize())
                .saved(dto.isSaved())
                .isTemplate(dto.isTemplate())
                .tags(dto.getTags())
                .build();
    }

    private ReportDTO mapToDTO(Report entity) {
        List<ReportDTO.SortDTO> sorting = entity.getSorting() != null ? entity.getSorting().stream()
                .map(s -> ReportDTO.SortDTO.builder().field(s.getField()).direction(s.getDirection()).build())
                .collect(Collectors.toList()) : Collections.emptyList();

        List<ReportDTO.AggregationDTO> aggregations = entity.getAggregations() != null ? entity.getAggregations().stream()
                .map(a -> ReportDTO.AggregationDTO.builder().field(a.getField()).function(a.getFunction()).alias(a.getAlias()).build())
                .collect(Collectors.toList()) : Collections.emptyList();

        return ReportDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .templateId(entity.getTemplateId())
                .selectedColumns(entity.getSelectedColumns())
                .filters(entity.getFilters())
                .sorting(sorting)
                .grouping(entity.getGrouping())
                .aggregations(aggregations)
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .pageNumber(entity.getPageNumber())
                .pageSize(entity.getPageSize())
                .saved(entity.isSaved())
                .isTemplate(entity.isTemplate())
                .tags(entity.getTags())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }
}
