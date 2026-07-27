package com.techknife.communication.service.impl;

import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.communication.dto.*;
import com.techknife.communication.entity.*;
import com.techknife.communication.repository.*;
import com.techknife.communication.service.NotificationService;
import com.techknife.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationQueueRepository queueRepository;
    private final EmailService emailService;

    @Override
    public List<NotificationDTO> getUserNotifications(String userId, String status) {
        List<Notification> list;
        if (status != null && !status.isBlank()) {
            list = notificationRepository.findByRecipientIdAndStatusOrderByCreatedAtDesc(userId, status);
        } else {
            list = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
        }
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public NotificationDTO sendNotification(SendNotificationRequest request) {
        String type = request.getType() != null ? request.getType() : "IN_APP";
        String body = request.getBody();
        String title = request.getTitle();

        if (request.getTemplateCode() != null && !request.getTemplateCode().isBlank()) {
            Optional<NotificationTemplate> tOpt = templateRepository.findByTemplateCode(request.getTemplateCode());
            if (tOpt.isPresent()) {
                NotificationTemplate template = tOpt.get();
                title = template.getSubject() != null ? template.getSubject() : title;
                body = renderTemplate(template.getBodyTemplate(), request.getTemplateVariables());
                type = template.getChannelType() != null ? template.getChannelType() : type;
            }
        }

        Notification notification = Notification.builder()
                .recipientId(request.getRecipientId())
                .recipientEmail(request.getRecipientEmail())
                .title(title)
                .body(body)
                .type(type)
                .category(request.getCategory() != null ? request.getCategory() : "GENERAL")
                .status("UNREAD")
                .linkUrl(request.getLinkUrl())
                .metadata(request.getMetadata())
                .createdAt(Instant.now())
                .build();

        Notification saved = notificationRepository.save(notification);

        // Queue for channel processing if not purely IN_APP or if recipientEmail exists
        if ("EMAIL".equalsIgnoreCase(type) && request.getRecipientEmail() != null) {
            try {
                emailService.sendSimpleEmail(request.getRecipientEmail(), title, body);
            } catch (Exception e) {
                log.warn("Failed to send email notification to {}: {}", request.getRecipientEmail(), e.getMessage());
            }
        }

        NotificationQueue queueItem = NotificationQueue.builder()
                .notificationId(saved.getId())
                .recipient(request.getRecipientEmail() != null ? request.getRecipientEmail() : request.getRecipientId())
                .channel(type)
                .status("SENT")
                .retries(0)
                .maxRetries(3)
                .scheduledAt(Instant.now())
                .processedAt(Instant.now())
                .createdAt(Instant.now())
                .build();
        queueRepository.save(queueItem);

        return mapToDTO(saved);
    }

    @Override
    public NotificationDTO markAsRead(String notificationId, String userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        notification.setStatus("READ");
        notification.setReadAt(Instant.now());
        return mapToDTO(notificationRepository.save(notification));
    }

    @Override
    public void markAllAsRead(String userId) {
        List<Notification> unreadList = notificationRepository.findByRecipientIdAndStatusOrderByCreatedAtDesc(userId, "UNREAD");
        Instant now = Instant.now();
        unreadList.forEach(n -> {
            n.setStatus("READ");
            n.setReadAt(now);
        });
        notificationRepository.saveAll(unreadList);
    }

    @Override
    public void deleteNotification(String id) {
        if (!notificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notification", "id", id);
        }
        notificationRepository.deleteById(id);
    }

    @Override
    public long getUnreadCount(String userId) {
        return notificationRepository.countByRecipientIdAndStatus(userId, "UNREAD");
    }

    // Templates
    @Override
    public NotificationTemplateDTO createTemplate(NotificationTemplateDTO dto) {
        NotificationTemplate template = NotificationTemplate.builder()
                .templateCode(dto.getTemplateCode())
                .name(dto.getName())
                .subject(dto.getSubject())
                .bodyTemplate(dto.getBodyTemplate())
                .channelType(dto.getChannelType() != null ? dto.getChannelType() : "EMAIL")
                .active(dto.isActive())
                .variables(dto.getVariables() != null ? dto.getVariables() : Collections.emptyList())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return mapToTemplateDTO(templateRepository.save(template));
    }

    @Override
    public NotificationTemplateDTO updateTemplate(String id, NotificationTemplateDTO dto) {
        NotificationTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NotificationTemplate", "id", id));
        template.setName(dto.getName());
        template.setSubject(dto.getSubject());
        template.setBodyTemplate(dto.getBodyTemplate());
        template.setChannelType(dto.getChannelType());
        template.setActive(dto.isActive());
        template.setVariables(dto.getVariables());
        template.setUpdatedAt(Instant.now());
        return mapToTemplateDTO(templateRepository.save(template));
    }

    @Override
    public NotificationTemplateDTO getTemplateByCode(String code) {
        NotificationTemplate template = templateRepository.findByTemplateCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("NotificationTemplate", "templateCode", code));
        return mapToTemplateDTO(template);
    }

    @Override
    public List<NotificationTemplateDTO> getAllTemplates() {
        return templateRepository.findAll().stream().map(this::mapToTemplateDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteTemplate(String id) {
        if (!templateRepository.existsById(id)) {
            throw new ResourceNotFoundException("NotificationTemplate", "id", id);
        }
        templateRepository.deleteById(id);
    }

    // Preferences
    @Override
    public NotificationPreferenceDTO getPreference(String userId) {
        NotificationPreference pref = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> preferenceRepository.save(NotificationPreference.builder()
                        .userId(userId)
                        .emailEnabled(true)
                        .pushEnabled(true)
                        .inAppEnabled(true)
                        .smsEnabled(false)
                        .categorySettings(new HashMap<>())
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build()));
        return mapToPreferenceDTO(pref);
    }

    @Override
    public NotificationPreferenceDTO updatePreference(String userId, NotificationPreferenceDTO dto) {
        NotificationPreference pref = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> NotificationPreference.builder().userId(userId).createdAt(Instant.now()).build());

        pref.setUserEmail(dto.getUserEmail());
        pref.setEmailEnabled(dto.isEmailEnabled());
        pref.setPushEnabled(dto.isPushEnabled());
        pref.setInAppEnabled(dto.isInAppEnabled());
        pref.setSmsEnabled(dto.isSmsEnabled());
        pref.setCategorySettings(dto.getCategorySettings());
        pref.setUpdatedAt(Instant.now());

        return mapToPreferenceDTO(preferenceRepository.save(pref));
    }

    // Queue
    @Override
    public List<NotificationQueueDTO> getQueuedNotifications() {
        return queueRepository.findAll().stream().map(this::mapToQueueDTO).collect(Collectors.toList());
    }

    @Override
    public NotificationQueueDTO retryNotification(String queueId) {
        NotificationQueue item = queueRepository.findById(queueId)
                .orElseThrow(() -> new ResourceNotFoundException("NotificationQueue", "id", queueId));
        item.setRetries(item.getRetries() + 1);
        item.setStatus("SENT");
        item.setProcessedAt(Instant.now());
        return mapToQueueDTO(queueRepository.save(item));
    }

    private String renderTemplate(String template, Map<String, Object> variables) {
        if (template == null || variables == null || variables.isEmpty()) {
            return template;
        }
        String result = template;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String key = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(key, value);
        }
        return result;
    }

    private NotificationDTO mapToDTO(Notification n) {
        return NotificationDTO.builder()
                .id(n.getId())
                .recipientId(n.getRecipientId())
                .recipientEmail(n.getRecipientEmail())
                .title(n.getTitle())
                .body(n.getBody())
                .type(n.getType())
                .category(n.getCategory())
                .status(n.getStatus())
                .linkUrl(n.getLinkUrl())
                .metadata(n.getMetadata())
                .readAt(n.getReadAt())
                .createdAt(n.getCreatedAt())
                .createdBy(n.getCreatedBy())
                .build();
    }

    private NotificationTemplateDTO mapToTemplateDTO(NotificationTemplate t) {
        return NotificationTemplateDTO.builder()
                .id(t.getId())
                .templateCode(t.getTemplateCode())
                .name(t.getName())
                .subject(t.getSubject())
                .bodyTemplate(t.getBodyTemplate())
                .channelType(t.getChannelType())
                .active(t.isActive())
                .variables(t.getVariables())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .createdBy(t.getCreatedBy())
                .build();
    }

    private NotificationPreferenceDTO mapToPreferenceDTO(NotificationPreference p) {
        return NotificationPreferenceDTO.builder()
                .id(p.getId())
                .userId(p.getUserId())
                .userEmail(p.getUserEmail())
                .emailEnabled(p.isEmailEnabled())
                .pushEnabled(p.isPushEnabled())
                .inAppEnabled(p.isInAppEnabled())
                .smsEnabled(p.isSmsEnabled())
                .categorySettings(p.getCategorySettings())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    private NotificationQueueDTO mapToQueueDTO(NotificationQueue q) {
        return NotificationQueueDTO.builder()
                .id(q.getId())
                .notificationId(q.getNotificationId())
                .recipient(q.getRecipient())
                .channel(q.getChannel())
                .status(q.getStatus())
                .retries(q.getRetries())
                .maxRetries(q.getMaxRetries())
                .errorMessage(q.getErrorMessage())
                .scheduledAt(q.getScheduledAt())
                .processedAt(q.getProcessedAt())
                .createdAt(q.getCreatedAt())
                .build();
    }
}
