package com.techknife.holiday.service.impl;

import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.holiday.dto.HolidayDTO;
import com.techknife.holiday.entity.Holiday;
import com.techknife.holiday.entity.HolidayType;
import com.techknife.holiday.repository.HolidayRepository;
import com.techknife.holiday.service.HolidayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository holidayRepository;

    @Override
    @Transactional
    public HolidayDTO createHoliday(HolidayDTO dto) {
        Holiday holiday = Holiday.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .date(dto.getDate())
                .year(dto.getYear() != null ? dto.getYear() : dto.getDate().getYear())
                .type(dto.getType())
                .branchId(dto.getBranchId())
                .state(dto.getState())
                .restricted(Boolean.TRUE.equals(dto.getRestricted()) || dto.getType() == HolidayType.RESTRICTED)
                .active(dto.getActive() == null || dto.getActive())
                .build();

        Holiday saved = holidayRepository.save(holiday);
        log.info("Created Holiday: ID={}, Name={}, Date={}", saved.getId(), saved.getName(), saved.getDate());
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public HolidayDTO updateHoliday(String id, HolidayDTO dto) {
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday not found with ID: " + id));

        if (dto.getName() != null) holiday.setName(dto.getName());
        if (dto.getDescription() != null) holiday.setDescription(dto.getDescription());
        if (dto.getDate() != null) {
            holiday.setDate(dto.getDate());
            holiday.setYear(dto.getYear() != null ? dto.getYear() : dto.getDate().getYear());
        }
        if (dto.getType() != null) holiday.setType(dto.getType());
        if (dto.getBranchId() != null) holiday.setBranchId(dto.getBranchId());
        if (dto.getState() != null) holiday.setState(dto.getState());
        if (dto.getRestricted() != null) holiday.setRestricted(dto.getRestricted());
        if (dto.getActive() != null) holiday.setActive(dto.getActive());

        Holiday updated = holidayRepository.save(holiday);
        log.info("Updated Holiday: ID={}, Name={}", updated.getId(), updated.getName());
        return mapToDTO(updated);
    }

    @Override
    public HolidayDTO getHolidayById(String id) {
        return holidayRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday not found with ID: " + id));
    }

    @Override
    public List<HolidayDTO> getHolidaysByYear(Integer year) {
        return holidayRepository.findByYear(year).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HolidayDTO> getHolidaysByYearAndBranch(Integer year, String branchId) {
        return holidayRepository.findByYearAndBranchId(year, branchId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HolidayDTO> getHolidaysByYearAndType(Integer year, HolidayType type) {
        return holidayRepository.findByYearAndType(year, type).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HolidayDTO> getRestrictedHolidays(Integer year) {
        return holidayRepository.findByYearAndRestrictedTrue(year).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HolidayDTO> getHolidaysInRange(LocalDate startDate, LocalDate endDate) {
        return holidayRepository.findByDateBetween(startDate, endDate).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HolidayDTO> getAllHolidays() {
        return holidayRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteHoliday(String id) {
        if (!holidayRepository.existsById(id)) {
            throw new ResourceNotFoundException("Holiday not found with ID: " + id);
        }
        holidayRepository.deleteById(id);
        log.info("Deleted Holiday ID={}", id);
    }

    private HolidayDTO mapToDTO(Holiday entity) {
        return HolidayDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .date(entity.getDate())
                .year(entity.getYear())
                .type(entity.getType())
                .branchId(entity.getBranchId())
                .state(entity.getState())
                .restricted(entity.getRestricted())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
