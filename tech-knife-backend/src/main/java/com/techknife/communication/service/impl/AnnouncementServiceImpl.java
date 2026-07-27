package com.techknife.communication.service.impl;

import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.communication.dto.AnnouncementCategoryDTO;
import com.techknife.communication.dto.AnnouncementDTO;
import com.techknife.communication.dto.AnnouncementReadDTO;
import com.techknife.communication.entity.Announcement;
import com.techknife.communication.entity.AnnouncementCategory;
import com.techknife.communication.entity.AnnouncementRead;
import com.techknife.communication.repository.AnnouncementCategoryRepository;
import com.techknife.communication.repository.AnnouncementReadRepository;
import com.techknife.communication.repository.AnnouncementRepository;
import com.techknife.communication.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementCategoryRepository categoryRepository;
    private final AnnouncementReadRepository readRepository;

    @Override
    public AnnouncementDTO createAnnouncement(AnnouncementDTO dto) {
        Announcement announcement = Announcement.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .categoryId(dto.getCategoryId())
                .categoryName(dto.getCategoryName())
                .status(dto.getStatus() != null ? dto.getStatus() : "DRAFT")
                .priority(dto.getPriority() != null ? dto.getPriority() : "MEDIUM")
                .targetType(dto.getTargetType() != null ? dto.getTargetType() : "ALL")
                .targetValues(dto.getTargetValues())
                .authorId(dto.getAuthorId())
                .authorName(dto.getAuthorName())
                .expiresAt(dto.getExpiresAt())
                .readCount(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        if ("PUBLISHED".equalsIgnoreCase(announcement.getStatus())) {
            announcement.setPublishedAt(Instant.now());
        }

        return mapToDTO(announcementRepository.save(announcement), null);
    }

    @Override
    public AnnouncementDTO updateAnnouncement(String id, AnnouncementDTO dto) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", id));

        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setCategoryId(dto.getCategoryId());
        announcement.setCategoryName(dto.getCategoryName());
        if (dto.getStatus() != null) {
            if ("PUBLISHED".equalsIgnoreCase(dto.getStatus()) && !"PUBLISHED".equalsIgnoreCase(announcement.getStatus())) {
                announcement.setPublishedAt(Instant.now());
            }
            announcement.setStatus(dto.getStatus());
        }
        if (dto.getPriority() != null) announcement.setPriority(dto.getPriority());
        if (dto.getTargetType() != null) announcement.setTargetType(dto.getTargetType());
        if (dto.getTargetValues() != null) announcement.setTargetValues(dto.getTargetValues());
        if (dto.getExpiresAt() != null) announcement.setExpiresAt(dto.getExpiresAt());

        announcement.setUpdatedAt(Instant.now());
        return mapToDTO(announcementRepository.save(announcement), null);
    }

    @Override
    public AnnouncementDTO publishAnnouncement(String id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", id));
        announcement.setStatus("PUBLISHED");
        announcement.setPublishedAt(Instant.now());
        announcement.setUpdatedAt(Instant.now());
        return mapToDTO(announcementRepository.save(announcement), null);
    }

    @Override
    public AnnouncementDTO archiveAnnouncement(String id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", id));
        announcement.setStatus("ARCHIVED");
        announcement.setUpdatedAt(Instant.now());
        return mapToDTO(announcementRepository.save(announcement), null);
    }

    @Override
    public void deleteAnnouncement(String id) {
        if (!announcementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Announcement", "id", id);
        }
        announcementRepository.deleteById(id);
    }

    @Override
    public AnnouncementDTO getAnnouncementById(String id, String currentUserId) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", id));
        return mapToDTO(announcement, currentUserId);
    }

    @Override
    public List<AnnouncementDTO> getAllAnnouncements(String status, String categoryId, String currentUserId) {
        List<Announcement> list;
        if (categoryId != null && !categoryId.isBlank()) {
            list = announcementRepository.findByCategoryId(categoryId);
        } else if (status != null && !status.isBlank()) {
            list = announcementRepository.findByStatusOrderByPublishedAtDesc(status);
        } else {
            list = announcementRepository.findAll();
        }
        return list.stream().map(a -> mapToDTO(a, currentUserId)).collect(Collectors.toList());
    }

    @Override
    public AnnouncementReadDTO markAsRead(String announcementId, String userId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", announcementId));

        boolean exists = readRepository.existsByAnnouncementIdAndUserId(announcementId, userId);
        AnnouncementRead read;
        if (!exists) {
            read = readRepository.save(AnnouncementRead.builder()
                    .announcementId(announcementId)
                    .userId(userId)
                    .readAt(Instant.now())
                    .build());
            announcement.setReadCount(announcement.getReadCount() + 1);
            announcementRepository.save(announcement);
        } else {
            read = readRepository.findByAnnouncementIdAndUserId(announcementId, userId).orElse(null);
        }

        return AnnouncementReadDTO.builder()
                .id(read != null ? read.getId() : null)
                .announcementId(announcementId)
                .userId(userId)
                .readAt(read != null ? read.getReadAt() : Instant.now())
                .build();
    }

    // Categories
    @Override
    public AnnouncementCategoryDTO createCategory(AnnouncementCategoryDTO dto) {
        AnnouncementCategory category = AnnouncementCategory.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .colorCode(dto.getColorCode())
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return mapToCategoryDTO(categoryRepository.save(category));
    }

    @Override
    public AnnouncementCategoryDTO updateCategory(String id, AnnouncementCategoryDTO dto) {
        AnnouncementCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AnnouncementCategory", "id", id));
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setColorCode(dto.getColorCode());
        category.setActive(dto.isActive());
        category.setUpdatedAt(Instant.now());
        return mapToCategoryDTO(categoryRepository.save(category));
    }

    @Override
    public List<AnnouncementCategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::mapToCategoryDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteCategory(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("AnnouncementCategory", "id", id);
        }
        categoryRepository.deleteById(id);
    }

    private AnnouncementDTO mapToDTO(Announcement a, String currentUserId) {
        boolean isRead = currentUserId != null && readRepository.existsByAnnouncementIdAndUserId(a.getId(), currentUserId);
        return AnnouncementDTO.builder()
                .id(a.getId())
                .title(a.getTitle())
                .content(a.getContent())
                .categoryId(a.getCategoryId())
                .categoryName(a.getCategoryName())
                .status(a.getStatus())
                .priority(a.getPriority())
                .targetType(a.getTargetType())
                .targetValues(a.getTargetValues())
                .authorId(a.getAuthorId())
                .authorName(a.getAuthorName())
                .publishedAt(a.getPublishedAt())
                .expiresAt(a.getExpiresAt())
                .readCount(a.getReadCount())
                .isReadByCurrentUser(isRead)
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .createdBy(a.getCreatedBy())
                .build();
    }

    private AnnouncementCategoryDTO mapToCategoryDTO(AnnouncementCategory c) {
        return AnnouncementCategoryDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .colorCode(c.getColorCode())
                .active(c.isActive())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .createdBy(c.getCreatedBy())
                .build();
    }
}
