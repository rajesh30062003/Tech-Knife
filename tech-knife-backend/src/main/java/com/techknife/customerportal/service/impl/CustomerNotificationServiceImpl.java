package com.techknife.customerportal.service.impl;

import com.techknife.customerportal.dto.CustomerNotificationDTO;
import com.techknife.customerportal.entity.CustomerNotification;
import com.techknife.customerportal.repository.CustomerNotificationRepository;
import com.techknife.customerportal.service.CustomerNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerNotificationServiceImpl implements CustomerNotificationService {

    private final CustomerNotificationRepository customerNotificationRepository;

    @Override
    public List<CustomerNotificationDTO> getNotifications(String customerAccountId) {
        return customerNotificationRepository.findByCustomerAccountIdOrderByCreatedAtDesc(customerAccountId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(String notificationId, String customerAccountId) {
        customerNotificationRepository.findById(notificationId).ifPresent(notification -> {
            if (notification.getCustomerAccountId().equals(customerAccountId)) {
                notification.setIsRead(true);
                customerNotificationRepository.save(notification);
            }
        });
    }

    @Override
    public void createNotification(String customerAccountId, String title, String message, String type, String linkUrl) {
        CustomerNotification notification = CustomerNotification.builder()
                .customerAccountId(customerAccountId)
                .title(title)
                .message(message)
                .type(type != null ? type : "ANNOUNCEMENT")
                .isRead(false)
                .linkUrl(linkUrl)
                .build();

        customerNotificationRepository.save(notification);
    }

    private CustomerNotificationDTO mapToDTO(CustomerNotification n) {
        return CustomerNotificationDTO.builder()
                .id(n.getId())
                .customerAccountId(n.getCustomerAccountId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .isRead(n.getIsRead())
                .linkUrl(n.getLinkUrl())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
