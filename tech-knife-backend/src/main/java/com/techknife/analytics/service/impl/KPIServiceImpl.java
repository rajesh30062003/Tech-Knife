package com.techknife.analytics.service.impl;

import com.techknife.analytics.dto.KPIDTO;
import com.techknife.analytics.dto.KPIGroupDTO;
import com.techknife.analytics.dto.KPIHistoryDTO;
import com.techknife.analytics.entity.KPI;
import com.techknife.analytics.entity.KPICategory;
import com.techknife.analytics.entity.KPIGroup;
import com.techknife.analytics.entity.KPIHistory;
import com.techknife.analytics.repository.KPIGroupRepository;
import com.techknife.analytics.repository.KPIHistoryRepository;
import com.techknife.analytics.repository.KPIRepository;
import com.techknife.analytics.service.KPIService;
import com.techknife.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KPIServiceImpl implements KPIService {

    private final KPIRepository kpiRepository;
    private final KPIGroupRepository kpiGroupRepository;
    private final KPIHistoryRepository kpiHistoryRepository;

    @Override
    public List<KPIDTO> getAllKPIs() {
        return kpiRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public KPIDTO getKPIById(String id) {
        KPI kpi = kpiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KPI not found with id: " + id));
        return mapToDTO(kpi);
    }

    @Override
    public KPIDTO getKPIByCode(String code) {
        KPI kpi = kpiRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("KPI not found with code: " + code));
        return mapToDTO(kpi);
    }

    @Override
    public List<KPIDTO> getKPIsByCategory(KPICategory category) {
        return kpiRepository.findByCategory(category).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public KPIDTO createKPI(KPIDTO dto) {
        KPI kpi = mapToEntity(dto);
        if (kpi.getCode() == null || kpi.getCode().isBlank()) {
            kpi.setCode("KPI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        kpi.setCreatedAt(Instant.now());
        kpi.setUpdatedAt(Instant.now());
        kpi.setActive(true);

        return mapToDTO(kpiRepository.save(kpi));
    }

    @Override
    public KPIDTO updateKPI(String id, KPIDTO dto) {
        KPI kpi = kpiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KPI not found with id: " + id));

        kpi.setName(dto.getName());
        kpi.setDescription(dto.getDescription());
        kpi.setCategory(dto.getCategory());
        kpi.setGroupId(dto.getGroupId());
        kpi.setCurrentValue(dto.getCurrentValue());
        kpi.setPreviousValue(dto.getPreviousValue());
        kpi.setTargetValue(dto.getTargetValue());
        kpi.setPercentageChange(dto.getPercentageChange());
        kpi.setUnit(dto.getUnit());
        kpi.setCalculationFormula(dto.getCalculationFormula());
        kpi.setDefaultDisplayType(dto.getDefaultDisplayType());
        kpi.setActive(dto.isActive());
        kpi.setUpdatedAt(Instant.now());

        return mapToDTO(kpiRepository.save(kpi));
    }

    @Override
    public void deleteKPI(String id) {
        if (!kpiRepository.existsById(id)) {
            throw new ResourceNotFoundException("KPI not found with id: " + id);
        }
        kpiRepository.deleteById(id);
    }

    @Override
    public KPIDTO refreshKPIValue(String id) {
        KPI kpi = kpiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KPI not found with id: " + id));

        // Save history entry
        KPIHistory history = KPIHistory.builder()
                .kpiId(kpi.getId())
                .kpiCode(kpi.getCode())
                .category(kpi.getCategory())
                .value(kpi.getCurrentValue())
                .targetValue(kpi.getTargetValue())
                .recordedAt(Instant.now())
                .createdAt(Instant.now())
                .build();
        kpiHistoryRepository.save(history);

        kpi.setUpdatedAt(Instant.now());
        return mapToDTO(kpiRepository.save(kpi));
    }

    @Override
    public List<KPIHistoryDTO> getKPIHistory(String kpiId) {
        return kpiHistoryRepository.findByKpiIdOrderByRecordedAtAsc(kpiId).stream()
                .map(this::mapHistoryToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public KPIGroupDTO createKPIGroup(KPIGroupDTO dto) {
        KPIGroup group = KPIGroup.builder()
                .name(dto.getName())
                .code(dto.getCode() != null ? dto.getCode() : "GRP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .icon(dto.getIcon())
                .displayOrder(dto.getDisplayOrder())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        KPIGroup saved = kpiGroupRepository.save(group);
        return KPIGroupDTO.builder()
                .id(saved.getId())
                .name(saved.getName())
                .code(saved.getCode())
                .description(saved.getDescription())
                .category(saved.getCategory())
                .icon(saved.getIcon())
                .displayOrder(saved.getDisplayOrder())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    @Override
    public List<KPIGroupDTO> getAllKPIGroups() {
        return kpiGroupRepository.findAll().stream().map(g -> KPIGroupDTO.builder()
                .id(g.getId())
                .name(g.getName())
                .code(g.getCode())
                .description(g.getDescription())
                .category(g.getCategory())
                .icon(g.getIcon())
                .displayOrder(g.getDisplayOrder())
                .createdAt(g.getCreatedAt())
                .updatedAt(g.getUpdatedAt())
                .build()).collect(Collectors.toList());
    }

    private KPIDTO mapToDTO(KPI entity) {
        if (entity == null) return null;
        return KPIDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .groupId(entity.getGroupId())
                .currentValue(entity.getCurrentValue())
                .previousValue(entity.getPreviousValue())
                .targetValue(entity.getTargetValue())
                .percentageChange(entity.getPercentageChange())
                .unit(entity.getUnit())
                .calculationFormula(entity.getCalculationFormula())
                .defaultDisplayType(entity.getDefaultDisplayType())
                .queryConfig(entity.getQueryConfig())
                .isSystem(entity.isSystem())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }

    private KPI mapToEntity(KPIDTO dto) {
        if (dto == null) return null;
        return KPI.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .groupId(dto.getGroupId())
                .currentValue(dto.getCurrentValue())
                .previousValue(dto.getPreviousValue())
                .targetValue(dto.getTargetValue())
                .percentageChange(dto.getPercentageChange())
                .unit(dto.getUnit())
                .calculationFormula(dto.getCalculationFormula())
                .defaultDisplayType(dto.getDefaultDisplayType())
                .queryConfig(dto.getQueryConfig())
                .isSystem(dto.isSystem())
                .active(dto.isActive())
                .build();
    }

    private KPIHistoryDTO mapHistoryToDTO(KPIHistory entity) {
        if (entity == null) return null;
        return KPIHistoryDTO.builder()
                .id(entity.getId())
                .kpiId(entity.getKpiId())
                .kpiCode(entity.getKpiCode())
                .category(entity.getCategory())
                .value(entity.getValue())
                .targetValue(entity.getTargetValue())
                .recordedAt(entity.getRecordedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
