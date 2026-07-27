package com.techknife.customerportal.service;

import com.techknife.customerportal.dto.SharedDocumentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SharedDocumentService {

    List<SharedDocumentDTO> getDocuments(String customerAccountId, String projectId, String category);

    SharedDocumentDTO uploadDocument(String customerAccountId, String projectId, String category, String documentName, String description, MultipartFile file);

    void deleteDocument(String documentId, String customerAccountId);
}
