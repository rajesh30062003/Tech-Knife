package com.techknife.asset.service.impl;

import com.techknife.asset.repository.AssetRepository;
import com.techknife.asset.repository.SoftwareLicenseRepository;
import com.techknife.asset.service.AssetSearchService;
import com.techknife.inventory.repository.InventoryItemRepository;
import com.techknife.inventory.repository.WarehouseRepository;
import com.techknife.procurement.repository.PurchaseOrderRepository;
import com.techknife.procurement.repository.PurchaseRequestRepository;
import com.techknife.procurement.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetSearchServiceImpl implements AssetSearchService {

    private final AssetRepository assetRepository;
    private final SoftwareLicenseRepository licenseRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final WarehouseRepository warehouseRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Override
    public Map<String, Object> globalSearch(String query) {
        Map<String, Object> results = new HashMap<>();
        if (query == null || query.isBlank()) {
            return results;
        }

        String q = query.toLowerCase();

        results.put("assets", assetRepository.findAll().stream()
                .filter(a -> (a.getName() != null && a.getName().toLowerCase().contains(q))
                        || (a.getAssetCode() != null && a.getAssetCode().toLowerCase().contains(q))
                        || (a.getSerialNumber() != null && a.getSerialNumber().toLowerCase().contains(q)))
                .limit(10)
                .collect(Collectors.toList()));

        results.put("licenses", licenseRepository.findAll().stream()
                .filter(l -> (l.getSoftwareName() != null && l.getSoftwareName().toLowerCase().contains(q))
                        || (l.getLicenseKey() != null && l.getLicenseKey().toLowerCase().contains(q)))
                .limit(10)
                .collect(Collectors.toList()));

        results.put("inventoryItems", inventoryItemRepository.findAll().stream()
                .filter(i -> (i.getName() != null && i.getName().toLowerCase().contains(q))
                        || (i.getItemCode() != null && i.getItemCode().toLowerCase().contains(q)))
                .limit(10)
                .collect(Collectors.toList()));

        results.put("warehouses", warehouseRepository.findAll().stream()
                .filter(w -> (w.getName() != null && w.getName().toLowerCase().contains(q))
                        || (w.getCode() != null && w.getCode().toLowerCase().contains(q)))
                .limit(10)
                .collect(Collectors.toList()));

        results.put("suppliers", supplierRepository.findAll().stream()
                .filter(s -> (s.getCompanyName() != null && s.getCompanyName().toLowerCase().contains(q))
                        || (s.getSupplierCode() != null && s.getSupplierCode().toLowerCase().contains(q)))
                .limit(10)
                .collect(Collectors.toList()));

        results.put("purchaseRequests", purchaseRequestRepository.findAll().stream()
                .filter(pr -> pr.getRequestNumber() != null && pr.getRequestNumber().toLowerCase().contains(q))
                .limit(10)
                .collect(Collectors.toList()));

        results.put("purchaseOrders", purchaseOrderRepository.findAll().stream()
                .filter(po -> po.getPoNumber() != null && po.getPoNumber().toLowerCase().contains(q))
                .limit(10)
                .collect(Collectors.toList()));

        return results;
    }
}
