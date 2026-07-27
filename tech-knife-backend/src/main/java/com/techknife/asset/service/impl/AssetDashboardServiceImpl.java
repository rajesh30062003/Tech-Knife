package com.techknife.asset.service.impl;

import com.techknife.asset.entity.Asset;
import com.techknife.asset.entity.SoftwareLicense;
import com.techknife.asset.repository.AssetMaintenanceRepository;
import com.techknife.asset.repository.AssetRepository;
import com.techknife.asset.repository.SoftwareLicenseRepository;
import com.techknife.asset.service.AssetDashboardService;
import com.techknife.inventory.entity.InventoryItem;
import com.techknife.inventory.entity.InventoryStock;
import com.techknife.inventory.repository.InventoryItemRepository;
import com.techknife.inventory.repository.InventoryStockRepository;
import com.techknife.procurement.repository.PurchaseRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AssetDashboardServiceImpl implements AssetDashboardService {

    private final AssetRepository assetRepository;
    private final AssetMaintenanceRepository maintenanceRepository;
    private final SoftwareLicenseRepository licenseRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryStockRepository inventoryStockRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;

    @Override
    public Map<String, Object> getDashboardMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        long totalAssets = assetRepository.count();
        long assignedAssets = assetRepository.findByStatus("ASSIGNED").size();
        long availableAssets = assetRepository.findByStatus("AVAILABLE").size();
        long maintenanceDue = maintenanceRepository.findByStatus("SCHEDULED").size();

        LocalDate today = LocalDate.now();
        LocalDate next30Days = today.plusDays(30);

        long expiringLicenses = licenseRepository.findAll().stream()
                .filter(l -> l.getExpiryDate() != null && !l.getExpiryDate().isBefore(today) && !l.getExpiryDate().isAfter(next30Days))
                .count();

        List<InventoryItem> items = inventoryItemRepository.findAll();
        int lowStockCount = 0;
        BigDecimal totalInventoryValue = BigDecimal.ZERO;

        for (InventoryItem item : items) {
            List<InventoryStock> stocks = inventoryStockRepository.findByItemId(item.getId());
            int totalQty = stocks.stream().mapToInt(s -> s.getQuantity() != null ? s.getQuantity() : 0).sum();
            int reorder = item.getReorderLevel() != null ? item.getReorderLevel() : 10;

            if (totalQty <= reorder) {
                lowStockCount++;
            }

            BigDecimal price = item.getPurchasePrice() != null ? item.getPurchasePrice() : BigDecimal.ZERO;
            totalInventoryValue = totalInventoryValue.add(price.multiply(BigDecimal.valueOf(totalQty)));
        }

        long pendingPurchaseRequests = purchaseRequestRepository.findByStatus("PENDING").size();

        metrics.put("totalAssets", totalAssets);
        metrics.put("assignedAssets", assignedAssets);
        metrics.put("availableAssets", availableAssets);
        metrics.put("maintenanceDue", maintenanceDue);
        metrics.put("expiringLicenses", expiringLicenses);
        metrics.put("lowStockItems", lowStockCount);
        metrics.put("pendingPurchaseRequests", pendingPurchaseRequests);
        metrics.put("totalInventoryValue", totalInventoryValue);

        return metrics;
    }
}
