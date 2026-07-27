package com.techknife.report.service.impl;

import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.report.dto.ReportScheduleDTO;
import com.techknife.report.entity.ReportSchedule;
import com.techknife.report.entity.ScheduleFrequency;
import com.techknife.report.repository.ReportScheduleRepository;
import com.techknife.report.service.ReportScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportScheduleServiceImpl implements ReportScheduleService {

    private final ReportScheduleRepository scheduleRepository;

    @Override
    public ReportScheduleDTO createSchedule(ReportScheduleDTO dto) {
        String cron = resolveCronExpression(dto.getFrequency(), dto.getCronExpression());

        // Check Schedule Conflicts
        if (scheduleRepository.existsByReportIdAndCronExpressionAndActiveTrue(dto.getReportId(), cron)) {
            throw new BadRequestException("Schedule Conflicts: An active schedule already exists for report ID '" 
                    + dto.getReportId() + "' with cron pattern '" + cron + "'");
        }

        ReportSchedule schedule = mapToEntity(dto);
        schedule.setCronExpression(cron);
        schedule.setActive(true);
        schedule.setNextRunTime(calculateNextRun(schedule.getFrequency()));

        ReportSchedule saved = scheduleRepository.save(schedule);
        return mapToDTO(saved);
    }

    @Override
    public ReportScheduleDTO updateSchedule(String id, ReportScheduleDTO dto) {
        ReportSchedule existing = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReportSchedule", "id", id));

        String cron = resolveCronExpression(dto.getFrequency(), dto.getCronExpression());

        if (!existing.getCronExpression().equalsIgnoreCase(cron) 
                && scheduleRepository.existsByReportIdAndCronExpressionAndActiveTrue(dto.getReportId(), cron)) {
            throw new BadRequestException("Schedule Conflicts: An active schedule already exists for report ID '" 
                    + dto.getReportId() + "' with cron pattern '" + cron + "'");
        }

        existing.setFrequency(dto.getFrequency());
        existing.setCronExpression(cron);
        existing.setExportFormat(dto.getExportFormat());
        existing.setEmailRecipients(dto.getEmailRecipients());
        existing.setSendEmail(dto.isSendEmail());
        existing.setStoreInCloud(dto.isStoreInCloud());
        existing.setNextRunTime(calculateNextRun(existing.getFrequency()));

        ReportSchedule updated = scheduleRepository.save(existing);
        return mapToDTO(updated);
    }

    @Override
    public ReportScheduleDTO getScheduleById(String id) {
        ReportSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReportSchedule", "id", id));
        return mapToDTO(schedule);
    }

    @Override
    public List<ReportScheduleDTO> getSchedulesByReportId(String reportId) {
        return scheduleRepository.findByReportId(reportId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportScheduleDTO> getAllActiveSchedules() {
        return scheduleRepository.findByActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportScheduleDTO> searchSchedules(String query) {
        if (query == null || query.trim().isEmpty()) {
            return scheduleRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
        }
        return scheduleRepository.findByReportNameContainingIgnoreCase(query.trim()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void toggleScheduleStatus(String id, boolean active) {
        ReportSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReportSchedule", "id", id));
        schedule.setActive(active);
        scheduleRepository.save(schedule);
    }

    @Override
    public void deleteSchedule(String id) {
        if (!scheduleRepository.existsById(id)) {
            throw new ResourceNotFoundException("ReportSchedule", "id", id);
        }
        scheduleRepository.deleteById(id);
    }

    @Override
    @Scheduled(cron = "0 */15 * * * *") // Check every 15 minutes
    public void processDueSchedules() {
        List<ReportSchedule> activeSchedules = scheduleRepository.findByActiveTrue();
        Instant now = Instant.now();

        for (ReportSchedule schedule : activeSchedules) {
            if (schedule.getNextRunTime() != null && schedule.getNextRunTime().isBefore(now)) {
                schedule.setLastRunTime(now);
                schedule.setLastExecutionStatus("SUCCESS");
                schedule.setNextRunTime(calculateNextRun(schedule.getFrequency()));
                scheduleRepository.save(schedule);
            }
        }
    }

    private String resolveCronExpression(ScheduleFrequency frequency, String customCron) {
        if (frequency == ScheduleFrequency.CUSTOM) {
            if (customCron == null || customCron.trim().isEmpty()) {
                throw new BadRequestException("Custom CRON expression is required when frequency is set to CUSTOM");
            }
            return customCron.trim();
        }

        switch (frequency) {
            case DAILY:
                return "0 0 0 * * *";
            case WEEKLY:
                return "0 0 0 * * MON";
            case MONTHLY:
                return "0 0 0 1 * *";
            case QUARTERLY:
                return "0 0 0 1 1,4,7,10 *";
            case YEARLY:
                return "0 0 0 1 1 *";
            default:
                return "0 0 0 * * *";
        }
    }

    private Instant calculateNextRun(ScheduleFrequency frequency) {
        long daysToAdd = 1;
        switch (frequency) {
            case DAILY:
                daysToAdd = 1;
                break;
            case WEEKLY:
                daysToAdd = 7;
                break;
            case MONTHLY:
                daysToAdd = 30;
                break;
            case QUARTERLY:
                daysToAdd = 90;
                break;
            case YEARLY:
                daysToAdd = 365;
                break;
            default:
                daysToAdd = 1;
                break;
        }
        return Instant.now().plusSeconds(daysToAdd * 86400);
    }

    private ReportSchedule mapToEntity(ReportScheduleDTO dto) {
        return ReportSchedule.builder()
                .id(dto.getId())
                .reportId(dto.getReportId())
                .reportName(dto.getReportName() != null ? dto.getReportName() : "Scheduled Report " + dto.getReportId())
                .frequency(dto.getFrequency())
                .cronExpression(dto.getCronExpression())
                .exportFormat(dto.getExportFormat())
                .emailRecipients(dto.getEmailRecipients())
                .sendEmail(dto.isSendEmail())
                .storeInCloud(dto.isStoreInCloud())
                .active(dto.isActive())
                .build();
    }

    private ReportScheduleDTO mapToDTO(ReportSchedule entity) {
        return ReportScheduleDTO.builder()
                .id(entity.getId())
                .reportId(entity.getReportId())
                .reportName(entity.getReportName())
                .frequency(entity.getFrequency())
                .cronExpression(entity.getCronExpression())
                .exportFormat(entity.getExportFormat())
                .emailRecipients(entity.getEmailRecipients())
                .sendEmail(entity.isSendEmail())
                .storeInCloud(entity.isStoreInCloud())
                .active(entity.isActive())
                .lastRunTime(entity.getLastRunTime())
                .nextRunTime(entity.getNextRunTime())
                .lastExecutionStatus(entity.getLastExecutionStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }
}
