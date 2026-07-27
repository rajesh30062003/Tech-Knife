package com.techknife.inventory.service.impl;

import com.techknife.inventory.dto.WarehouseDTO;
import com.techknife.inventory.entity.Warehouse;
import com.techknife.inventory.repository.WarehouseRepository;
import com.techknife.inventory.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Override
    public List<WarehouseDTO> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public WarehouseDTO getWarehouseById(String id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found with id: " + id));
        return mapToDTO(warehouse);
    }

    @Override
    public WarehouseDTO createWarehouse(WarehouseDTO dto) {
        if (warehouseRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("Warehouse already exists with code: " + dto.getCode());
        }

        Warehouse warehouse = Warehouse.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .location(dto.getLocation())
                .capacity(dto.getCapacity())
                .managerId(dto.getManagerId())
                .managerName(dto.getManagerName())
                .contactPhone(dto.getContactPhone())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        Warehouse saved = warehouseRepository.save(warehouse);
        return mapToDTO(saved);
    }

    @Override
    public WarehouseDTO updateWarehouse(String id, WarehouseDTO dto) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found with id: " + id));

        if (dto.getName() != null) warehouse.setName(dto.getName());
        if (dto.getLocation() != null) warehouse.setLocation(dto.getLocation());
        if (dto.getCapacity() != null) warehouse.setCapacity(dto.getCapacity());
        if (dto.getManagerId() != null) warehouse.setManagerId(dto.getManagerId());
        if (dto.getManagerName() != null) warehouse.setManagerName(dto.getManagerName());
        if (dto.getContactPhone() != null) warehouse.setContactPhone(dto.getContactPhone());
        if (dto.getStatus() != null) warehouse.setStatus(dto.getStatus());

        Warehouse saved = warehouseRepository.save(warehouse);
        return mapToDTO(saved);
    }

    @Override
    public void deleteWarehouse(String id) {
        if (!warehouseRepository.existsById(id)) {
            throw new IllegalArgumentException("Warehouse not found with id: " + id);
        }
        warehouseRepository.deleteById(id);
    }

    private WarehouseDTO mapToDTO(Warehouse w) {
        return WarehouseDTO.builder()
                .id(w.getId())
                .code(w.getCode())
                .name(w.getName())
                .location(w.getLocation())
                .capacity(w.getCapacity())
                .managerId(w.getManagerId())
                .managerName(w.getManagerName())
                .contactPhone(w.getContactPhone())
                .status(w.getStatus())
                .createdAt(w.getCreatedAt())
                .updatedAt(w.getUpdatedAt())
                .createdBy(w.getCreatedBy())
                .updatedBy(w.getUpdatedBy())
                .build();
    }
}
