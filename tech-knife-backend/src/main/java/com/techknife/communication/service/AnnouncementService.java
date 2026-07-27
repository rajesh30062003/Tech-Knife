package com.techknife.communication.service;

import com.techknife.communication.dto.AnnouncementCategoryDTO;
import com.techknife.communication.dto.AnnouncementDTO;
import com.techknife.communication.dto.AnnouncementReadDTO;

import java.util.List;

public interface AnnouncementService {

    AnnouncementDTO createAnnouncement(AnnouncementDTO dto);

    AnnouncementDTO updateAnnouncement(String id, AnnouncementDTO dto);

    AnnouncementDTO publishAnnouncement(String id);

    AnnouncementDTO archiveAnnouncement(String id);

    void deleteAnnouncement(String id);

    AnnouncementDTO getAnnouncementById(String id, String currentUserId);

    List<AnnouncementDTO> getAllAnnouncements(String status, String categoryId, String currentUserId);

    AnnouncementReadDTO markAsRead(String announcementId, String userId);

    // Categories
    AnnouncementCategoryDTO createCategory(AnnouncementCategoryDTO dto);

    AnnouncementCategoryDTO updateCategory(String id, AnnouncementCategoryDTO dto);

    List<AnnouncementCategoryDTO> getAllCategories();

    void deleteCategory(String id);
}
