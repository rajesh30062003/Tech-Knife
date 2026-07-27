package com.techknife.report.service.impl;

import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.report.dto.ReportHistoryDTO;
import com.techknife.report.entity.ReportHistory;
import com.techknife.report.repository.ReportHistoryRepository;
import com.techknife.report.service.ReportHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportHistoryServiceImpl implements ReportHistoryService {

    private final ReportHistoryRepository historyRepository;

    @Override
    public ReportHistoryDTO recordHistory(ReportHistoryDTO dto) {
        ReportHistory history = ReportHistory.builder()
                .reportId(dto.getReportId())
                .reportName(dto.getReportName())
                .executionId(dto.getExecutionId())
                .generatedBy(dto.getGeneratedBy() != null ? dto.getGeneratedBy() : "SYSTEM")
                .generatedAt(dto.getGeneratedAt() != null ? dto.getGeneratedAt() : Instant.now())
                .executionStatus(dto.getExecutionStatus())
                .downloadLink(dto.getDownloadLink())
                .fileSize(dto.getFileSize())
                .durationMs(dto.getDurationMs())
                .fileFormat(dto.getFileFormat())
                .build();

        ReportHistory saved = historyRepository.save(history);
        return mapToDTO(saved);
    }

    @Override
    public ReportHistoryDTO getHistoryById(String id) {
        ReportHistory history = historyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReportHistory", "id", id));
        return mapToDTO(history);
    }

    @Override
    public List<ReportHistoryDTO> getHistoryByReport(String reportId) {
        return historyRepository.findByReportId(reportId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportHistoryDTO> getHistoryByUser(String userId) {
        return historyRepository.findByGeneratedBy(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportHistoryDTO> searchHistory(String query) {
        if (query == null || query.trim().isEmpty()) {
            return historyRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
        }
        return historyRepository.findByReportNameContainingIgnoreCase(query.trim()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ReportHistoryDTO mapToDTO(ReportHistory entity) {
        return ReportHistoryDTO.builder()
                .id(entity.getId())
                .reportId(entity.getReportId())
                .reportName(entity.getReportName())
                .executionId(entity.getExecutionId())
                .generatedBy(entity.getGeneratedBy())
                .generatedAt(entity.getGeneratedAt())
                .executionStatus(entity.getExecutionStatus())
                .downloadLink(entity.getDownloadLink())
                .fileSize(entity.getFileSize())
                .durationMs(entity.getDurationMs())
                .fileFormat(entity.getFileFormat())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
