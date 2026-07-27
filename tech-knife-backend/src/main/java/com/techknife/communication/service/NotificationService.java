package com.techknife.communication.service;

import com.techknife.communication.dto.*;

import java.util.List;

public interface NotificationService {

    List<NotificationDTO> getUserNotifications(String userId, String status);

    NotificationDTO sendNotification(SendNotificationRequest request);

    NotificationDTO markAsRead(String notificationId, String userId);

    void markAllAsRead(String userId);

    void deleteNotification(String id);

    long getUnreadCount(String userId);

    // Templates
    NotificationTemplateDTO createTemplate(NotificationTemplateDTO dto);

    NotificationTemplateDTO updateTemplate(String id, NotificationTemplateDTO dto);

    NotificationTemplateDTO getTemplateByCode(String code);

    List<NotificationTemplateDTO> getAllTemplates();

    void deleteTemplate(String id);

    // Preferences
    NotificationPreferenceDTO getPreference(String userId);

    NotificationPreferenceDTO updatePreference(String userId, NotificationPreferenceDTO dto);

    // Queue
    List<NotificationQueueDTO> getQueuedNotifications();

    NotificationQueueDTO retryNotification(String queueId);
}
