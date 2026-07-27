package com.techknife.payroll.service.impl;

import com.techknife.payroll.dto.BonusDTO;
import com.techknife.payroll.entity.Bonus;
import com.techknife.payroll.repository.BonusRepository;
import com.techknife.payroll.service.BonusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BonusServiceImpl implements BonusService {

    private final BonusRepository bonusRepository;

    @Override
    public List<BonusDTO> getAllBonuses() {
        return bonusRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BonusDTO> getBonusesByEmployeeId(String employeeId) {
        return bonusRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BonusDTO getBonusById(String id) {
        Bonus bonus = bonusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bonus record not found with id: " + id));
        return mapToDTO(bonus);
    }

    @Override
    public BonusDTO createBonus(BonusDTO dto) {
        Bonus bonus = Bonus.builder()
                .employeeId(dto.getEmployeeId())
                .employeeName(dto.getEmployeeName())
                .bonusType(dto.getBonusType() != null ? dto.getBonusType() : "PERFORMANCE")
                .amount(dto.getAmount())
                .paymentDate(dto.getPaymentDate())
                .status(dto.getStatus() != null ? dto.getStatus() : "PENDING")
                .build();

        Bonus saved = bonusRepository.save(bonus);
        return mapToDTO(saved);
    }

    @Override
    public BonusDTO updateBonusStatus(String id, String status) {
        Bonus bonus = bonusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bonus record not found with id: " + id));

        bonus.setStatus(status != null ? status.toUpperCase() : "PENDING");
        Bonus saved = bonusRepository.save(bonus);
        return mapToDTO(saved);
    }

    private BonusDTO mapToDTO(Bonus b) {
        return BonusDTO.builder()
                .id(b.getId())
                .employeeId(b.getEmployeeId())
                .employeeName(b.getEmployeeName())
                .bonusType(b.getBonusType())
                .amount(b.getAmount())
                .paymentDate(b.getPaymentDate())
                .status(b.getStatus())
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .createdBy(b.getCreatedBy())
                .updatedBy(b.getUpdatedBy())
                .build();
    }
}
