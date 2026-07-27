package com.techknife.asset.service;

import java.util.Map;

public interface AssetReportService {
    Map<String, Object> getAssetRegisterReport();
    Map<String, Object> getAssignedAssetsReport();
    Map<String, Object> getAssetUtilizationReport();
    Map<String, Object> getWarrantyExpiryReport();
    Map<String, Object> getMaintenanceReport();
    Map<String, Object> getLicenseExpiryReport();
    Map<String, Object> getInventoryStockReport();
    Map<String, Object> getLowStockReport();
    Map<String, Object> getPurchaseReport();
    Map<String, Object> getSupplierReport();
}
