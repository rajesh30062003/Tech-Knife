package com.techknife.customerportal.service.impl;

import com.techknife.customerportal.dto.SharedDocumentDTO;
import com.techknife.customerportal.entity.CustomerAccount;
import com.techknife.customerportal.entity.SharedDocument;
import com.techknife.customerportal.repository.CustomerAccountRepository;
import com.techknife.customerportal.repository.SharedDocumentRepository;
import com.techknife.customerportal.service.SharedDocumentService;
import com.techknife.storage.FileStorageService;
import com.techknife.storage.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SharedDocumentServiceImpl implements SharedDocumentService {

    private final SharedDocumentRepository sharedDocumentRepository;
    private final CustomerAccountRepository customerAccountRepository;
    private final FileStorageService fileStorageService;

    @Override
    public List<SharedDocumentDTO> getDocuments(String customerAccountId, String projectId, String category) {
        List<SharedDocument> docs;

        if (projectId != null && !projectId.isBlank()) {
            docs = sharedDocumentRepository.findByProjectId(projectId);
        } else if (category != null && !category.isBlank()) {
            docs = sharedDocumentRepository.findByCustomerAccountIdAndCategory(customerAccountId, category.toUpperCase());
        } else {
            docs = sharedDocumentRepository.findByCustomerAccountId(customerAccountId);
        }

        return docs.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public SharedDocumentDTO uploadDocument(String customerAccountId, String projectId, String category, String documentName, String description, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        CustomerAccount account = customerAccountRepository.findById(customerAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Customer account not found"));

        FileUploadResponse upload = fileStorageService.uploadFile(file, "customer_portal/documents");

        SharedDocument doc = SharedDocument.builder()
                .customerAccountId(customerAccountId)
                .projectId(projectId)
                .documentName(documentName != null && !documentName.isBlank() ? documentName : upload.getOriginalFilename())
                .description(description)
                .fileUrl(upload.getSecureUrl())
                .cloudinaryPublicId(upload.getPublicId())
                .fileType(file.getContentType())
                .fileSize(upload.getBytes())
                .category(category != null ? category.toUpperCase() : "PROJECT_DOC")
                .uploadedBy(account.getContactPersonName())
                .build();

        SharedDocument saved = sharedDocumentRepository.save(doc);
        return mapToDTO(saved);
    }

    @Override
    public void deleteDocument(String documentId, String customerAccountId) {
        SharedDocument doc = sharedDocumentRepository.findByIdAndCustomerAccountId(documentId, customerAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found or access denied"));

        if (doc.getCloudinaryPublicId() != null) {
            try {
                fileStorageService.deleteFile(doc.getCloudinaryPublicId());
            } catch (Exception e) {
                log.warn("Failed to delete document from Cloudinary: {}", e.getMessage());
            }
        }

        sharedDocumentRepository.delete(doc);
    }

    private SharedDocumentDTO mapToDTO(SharedDocument doc) {
        return SharedDocumentDTO.builder()
                .id(doc.getId())
                .customerAccountId(doc.getCustomerAccountId())
                .projectId(doc.getProjectId())
                .documentName(doc.getDocumentName())
                .description(doc.getDescription())
                .fileUrl(doc.getFileUrl())
                .cloudinaryPublicId(doc.getCloudinaryPublicId())
                .fileType(doc.getFileType())
                .fileSize(doc.getFileSize())
                .category(doc.getCategory())
                .uploadedBy(doc.getUploadedBy())
                .uploadedAt(doc.getUploadedAt())
                .build();
    }
}
