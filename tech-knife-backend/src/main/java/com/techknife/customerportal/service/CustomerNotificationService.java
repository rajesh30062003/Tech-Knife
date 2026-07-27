package com.techknife.customerportal.service;

import com.techknife.customerportal.dto.CustomerNotificationDTO;

import java.util.List;

public interface CustomerNotificationService {

    List<CustomerNotificationDTO> getNotifications(String customerAccountId);

    void markAsRead(String notificationId, String customerAccountId);

    void createNotification(String customerAccountId, String title, String message, String type, String linkUrl);
}
