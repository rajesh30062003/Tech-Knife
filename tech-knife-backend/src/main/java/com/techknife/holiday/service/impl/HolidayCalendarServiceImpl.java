package com.techknife.holiday.service.impl;

import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.holiday.dto.HolidayCalendarDTO;
import com.techknife.holiday.entity.HolidayCalendar;
import com.techknife.holiday.repository.HolidayCalendarRepository;
import com.techknife.holiday.repository.HolidayRepository;
import com.techknife.holiday.service.HolidayCalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayCalendarServiceImpl implements HolidayCalendarService {

    private final HolidayCalendarRepository calendarRepository;
    private final HolidayRepository holidayRepository;

    @Override
    @Transactional
    public HolidayCalendarDTO createCalendar(HolidayCalendarDTO dto) {
        if (dto.getBranchId() != null && calendarRepository.findByYearAndBranchId(dto.getYear(), dto.getBranchId()).isPresent()) {
            throw new BadRequestException("Holiday Calendar already exists for year " + dto.getYear() + " and branch " + dto.getBranchId());
        }

        HolidayCalendar calendar = HolidayCalendar.builder()
                .name(dto.getName())
                .year(dto.getYear())
                .branchId(dto.getBranchId())
                .branchName(dto.getBranchName())
                .holidayIds(dto.getHolidayIds() != null ? dto.getHolidayIds() : new ArrayList<>())
                .maxRestrictedHolidaysAllowed(dto.getMaxRestrictedHolidaysAllowed() != null ? dto.getMaxRestrictedHolidaysAllowed() : 2)
                .active(dto.getActive() == null || dto.getActive())
                .build();

        HolidayCalendar saved = calendarRepository.save(calendar);
        log.info("Created HolidayCalendar ID={}, Name={}", saved.getId(), saved.getName());
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public HolidayCalendarDTO updateCalendar(String id, HolidayCalendarDTO dto) {
        HolidayCalendar calendar = calendarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday Calendar not found with ID: " + id));

        if (dto.getName() != null) calendar.setName(dto.getName());
        if (dto.getBranchName() != null) calendar.setBranchName(dto.getBranchName());
        if (dto.getHolidayIds() != null) calendar.setHolidayIds(dto.getHolidayIds());
        if (dto.getMaxRestrictedHolidaysAllowed() != null) calendar.setMaxRestrictedHolidaysAllowed(dto.getMaxRestrictedHolidaysAllowed());
        if (dto.getActive() != null) calendar.setActive(dto.getActive());

        HolidayCalendar updated = calendarRepository.save(calendar);
        log.info("Updated HolidayCalendar ID={}", updated.getId());
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public HolidayCalendarDTO addHolidayToCalendar(String calendarId, String holidayId) {
        HolidayCalendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday Calendar not found with ID: " + calendarId));

        if (!holidayRepository.existsById(holidayId)) {
            throw new ResourceNotFoundException("Holiday not found with ID: " + holidayId);
        }

        if (!calendar.getHolidayIds().contains(holidayId)) {
            calendar.getHolidayIds().add(holidayId);
            calendar = calendarRepository.save(calendar);
        }

        return mapToDTO(calendar);
    }

    @Override
    @Transactional
    public HolidayCalendarDTO removeHolidayFromCalendar(String calendarId, String holidayId) {
        HolidayCalendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday Calendar not found with ID: " + calendarId));

        calendar.getHolidayIds().remove(holidayId);
        calendar = calendarRepository.save(calendar);
        return mapToDTO(calendar);
    }

    @Override
    public HolidayCalendarDTO getCalendarById(String id) {
        return calendarRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday Calendar not found with ID: " + id));
    }

    @Override
    public HolidayCalendarDTO getCalendarByYearAndBranch(Integer year, String branchId) {
        return calendarRepository.findByYearAndBranchId(year, branchId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday Calendar not found for year " + year + " and branch " + branchId));
    }

    @Override
    public List<HolidayCalendarDTO> getCalendarsByYear(Integer year) {
        return calendarRepository.findByYear(year).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HolidayCalendarDTO> getAllCalendars() {
        return calendarRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCalendar(String id) {
        if (!calendarRepository.existsById(id)) {
            throw new ResourceNotFoundException("Holiday Calendar not found with ID: " + id);
        }
        calendarRepository.deleteById(id);
        log.info("Deleted HolidayCalendar ID={}", id);
    }

    private HolidayCalendarDTO mapToDTO(HolidayCalendar entity) {
        return HolidayCalendarDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .year(entity.getYear())
                .branchId(entity.getBranchId())
                .branchName(entity.getBranchName())
                .holidayIds(entity.getHolidayIds())
                .maxRestrictedHolidaysAllowed(entity.getMaxRestrictedHolidaysAllowed())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
