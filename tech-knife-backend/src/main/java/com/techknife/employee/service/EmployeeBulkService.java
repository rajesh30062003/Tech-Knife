package com.techknife.employee.service;

import com.techknife.employee.dto.BulkDepartmentTransferRequest;
import com.techknife.employee.dto.BulkOperationResponse;
import com.techknife.employee.dto.BulkStatusChangeRequest;
import com.techknife.employee.entity.EmployeeStatus;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeBulkService {

    BulkOperationResponse bulkImport(MultipartFile file, String importedBy);

    byte[] bulkExportCSV(String departmentId, EmployeeStatus status);

    BulkOperationResponse bulkStatusChange(BulkStatusChangeRequest request, String updatedBy);

    BulkOperationResponse bulkDepartmentTransfer(BulkDepartmentTransferRequest request, String updatedBy);
}
