package com.techknife.report.service.impl;

import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.report.dto.ReportExecutionDTO;
import com.techknife.report.entity.ExecutionStatus;
import com.techknife.report.entity.ExportFormat;
import com.techknife.report.entity.ReportExecution;
import com.techknife.report.repository.ReportExecutionRepository;
import com.techknife.report.service.ReportExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportExecutionServiceImpl implements ReportExecutionService {

    private final ReportExecutionRepository executionRepository;

    @Override
    public ReportExecutionDTO queueExecution(String reportId, String scheduleId, ExportFormat format) {
        ReportExecution execution = ReportExecution.builder()
                .reportId(reportId)
                .reportName("Execution_" + reportId)
                .scheduleId(scheduleId)
                .format(format != null ? format : ExportFormat.PDF)
                .status(ExecutionStatus.QUEUED)
                .progressPercentage(0)
                .retryCount(0)
                .maxRetries(3)
                .startedAt(Instant.now())
                .build();

        ReportExecution saved = executionRepository.save(execution);
        return mapToDTO(saved);
    }

    @Override
    public ReportExecutionDTO getExecutionStatus(String executionId) {
        ReportExecution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new ResourceNotFoundException("ReportExecution", "id", executionId));
        return mapToDTO(execution);
    }

    @Override
    public List<ReportExecutionDTO> getExecutionsByReport(String reportId) {
        return executionRepository.findByReportId(reportId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportExecutionDTO> getExecutionsByStatus(ExecutionStatus status) {
        return executionRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReportExecutionDTO retryFailedJob(String executionId) {
        ReportExecution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new ResourceNotFoundException("ReportExecution", "id", executionId));

        if (execution.getStatus() != ExecutionStatus.FAILED) {
            throw new BadRequestException("Only failed executions can be retried");
        }

        execution.setStatus(ExecutionStatus.RETRYING);
        execution.setRetryCount(execution.getRetryCount() + 1);
        execution.setErrorMessage(null);
        execution.setProgressPercentage(10);

        ReportExecution saved = executionRepository.save(execution);
        return mapToDTO(saved);
    }

    @Override
    public void cancelExecution(String executionId) {
        ReportExecution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new ResourceNotFoundException("ReportExecution", "id", executionId));

        if (execution.getStatus() == ExecutionStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel an execution that is already completed");
        }

        execution.setStatus(ExecutionStatus.CANCELLED);
        executionRepository.save(execution);
    }

    @Override
    @Scheduled(fixedDelay = 5000) // Poll execution queue every 5 seconds
    public void processQueuedExecutions() {
        List<ReportExecution> queued = executionRepository.findByStatus(ExecutionStatus.QUEUED);
        List<ReportExecution> retrying = executionRepository.findByStatus(ExecutionStatus.RETRYING);

        queued.addAll(retrying);

        for (ReportExecution execution : queued) {
            long startTime = System.currentTimeMillis();
            try {
                execution.setStatus(ExecutionStatus.PROCESSING);
                execution.setProgressPercentage(50);
                executionRepository.save(execution);

                // Simulate processing heavy enterprise report data
                Thread.sleep(100);

                long duration = System.currentTimeMillis() - startTime;
                execution.setStatus(ExecutionStatus.COMPLETED);
                execution.setProgressPercentage(100);
                execution.setCompletedAt(Instant.now());
                execution.setDurationMs(duration);
                execution.setFileUrl("https://res.cloudinary.com/techknife/raw/upload/executions/" + execution.getId() + ".pdf");
                execution.setFileSize(204850L);

                executionRepository.save(execution);

            } catch (Exception e) {
                execution.setStatus(ExecutionStatus.FAILED);
                execution.setErrorMessage(e.getMessage());
                executionRepository.save(execution);
            }
        }
    }

    private ReportExecutionDTO mapToDTO(ReportExecution entity) {
        return ReportExecutionDTO.builder()
                .id(entity.getId())
                .reportId(entity.getReportId())
                .reportName(entity.getReportName())
                .scheduleId(entity.getScheduleId())
                .format(entity.getFormat())
                .status(entity.getStatus())
                .progressPercentage(entity.getProgressPercentage())
                .errorMessage(entity.getErrorMessage())
                .retryCount(entity.getRetryCount())
                .maxRetries(entity.getMaxRetries())
                .fileUrl(entity.getFileUrl())
                .fileSize(entity.getFileSize())
                .durationMs(entity.getDurationMs())
                .startedAt(entity.getStartedAt())
                .completedAt(entity.getCompletedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }
}
