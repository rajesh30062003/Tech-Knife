package com.techknife.asset.service.impl;

import com.techknife.asset.entity.Asset;
import com.techknife.asset.entity.SoftwareLicense;
import com.techknife.asset.repository.*;
import com.techknife.asset.service.AssetReportService;
import com.techknife.inventory.entity.InventoryItem;
import com.techknife.inventory.entity.InventoryStock;
import com.techknife.inventory.repository.InventoryItemRepository;
import com.techknife.inventory.repository.InventoryStockRepository;
import com.techknife.procurement.repository.PurchaseOrderRepository;
import com.techknife.procurement.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetReportServiceImpl implements AssetReportService {

    private final AssetRepository assetRepository;
    private final AssetAssignmentRepository assignmentRepository;
    private final AssetMaintenanceRepository maintenanceRepository;
    private final AssetWarrantyRepository warrantyRepository;
    private final SoftwareLicenseRepository licenseRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryStockRepository inventoryStockRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;

    @Override
    public Map<String, Object> getAssetRegisterReport() {
        Map<String, Object> report = new HashMap<>();
        List<Asset> assets = assetRepository.findAll();
        report.put("totalCount", assets.size());
        report.put("assets", assets);
        return report;
    }

    @Override
    public Map<String, Object> getAssignedAssetsReport() {
        Map<String, Object> report = new HashMap<>();
        List<Asset> assigned = assetRepository.findByStatus("ASSIGNED");
        report.put("assignedCount", assigned.size());
        report.put("assets", assigned);
        return report;
    }

    @Override
    public Map<String, Object> getAssetUtilizationReport() {
        Map<String, Object> report = new HashMap<>();
        long total = assetRepository.count();
        long assigned = assetRepository.findByStatus("ASSIGNED").size();
        long available = assetRepository.findByStatus("AVAILABLE").size();
        long maintenance = assetRepository.findByStatus("UNDER_MAINTENANCE").size();

        double utilizationRate = total > 0 ? ((double) assigned / total) * 100 : 0.0;

        report.put("totalAssets", total);
        report.put("assignedAssets", assigned);
        report.put("availableAssets", available);
        report.put("maintenanceAssets", maintenance);
        report.put("utilizationPercentage", utilizationRate);
        return report;
    }

    @Override
    public Map<String, Object> getWarrantyExpiryReport() {
        Map<String, Object> report = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate next30Days = today.plusDays(30);

        List<Asset> expiring = assetRepository.findAll().stream()
                .filter(a -> a.getWarrantyEndDate() != null && !a.getWarrantyEndDate().isBefore(today) && !a.getWarrantyEndDate().isAfter(next30Days))
                .collect(Collectors.toList());

        report.put("expiringIn30DaysCount", expiring.size());
        report.put("assets", expiring);
        return report;
    }

    @Override
    public Map<String, Object> getMaintenanceReport() {
        Map<String, Object> report = new HashMap<>();
        report.put("maintenances", maintenanceRepository.findAll());
        return report;
    }

    @Override
    public Map<String, Object> getLicenseExpiryReport() {
        Map<String, Object> report = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate next30Days = today.plusDays(30);

        List<SoftwareLicense> expiring = licenseRepository.findAll().stream()
                .filter(l -> l.getExpiryDate() != null && !l.getExpiryDate().isBefore(today) && !l.getExpiryDate().isAfter(next30Days))
                .collect(Collectors.toList());

        report.put("expiringLicensesCount", expiring.size());
        report.put("licenses", expiring);
        return report;
    }

    @Override
    public Map<String, Object> getInventoryStockReport() {
        Map<String, Object> report = new HashMap<>();
        report.put("stocks", inventoryStockRepository.findAll());
        return report;
    }

    @Override
    public Map<String, Object> getLowStockReport() {
        Map<String, Object> report = new HashMap<>();
        List<InventoryItem> items = inventoryItemRepository.findAll();
        List<Map<String, Object>> lowStockItems = new ArrayList<>();

        for (InventoryItem item : items) {
            List<InventoryStock> stocks = inventoryStockRepository.findByItemId(item.getId());
            int totalQty = stocks.stream().mapToInt(s -> s.getQuantity() != null ? s.getQuantity() : 0).sum();
            int reorder = item.getReorderLevel() != null ? item.getReorderLevel() : 10;

            if (totalQty <= reorder) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("item", item);
                itemMap.put("currentStock", totalQty);
                itemMap.put("reorderLevel", reorder);
                lowStockItems.add(itemMap);
            }
        }

        report.put("lowStockCount", lowStockItems.size());
        report.put("items", lowStockItems);
        return report;
    }

    @Override
    public Map<String, Object> getPurchaseReport() {
        Map<String, Object> report = new HashMap<>();
        report.put("purchaseOrders", purchaseOrderRepository.findAll());
        return report;
    }

    @Override
    public Map<String, Object> getSupplierReport() {
        Map<String, Object> report = new HashMap<>();
        report.put("suppliers", supplierRepository.findAll());
        return report;
    }
}
