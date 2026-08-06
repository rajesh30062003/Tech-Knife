package com.techknife.communication.service.impl;

import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.communication.dto.*;
import com.techknife.communication.entity.*;
import com.techknife.communication.repository.*;
import com.techknife.communication.service.InternalMessageService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalMessageServiceImpl implements InternalMessageService {

    private final InternalMessageRepository messageRepository;
    private final MessageThreadRepository threadRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public InternalMessageDTO sendMessage(SendMessageRequest request, String senderId, String senderName) {
        log.info("CHAT REQUEST subject={}, threadId={}, projectCode={}, content={}",
                request.getSubject(),
                request.getThreadId(),
                request.getSubject(),
                request.getContent());

        MessageThread thread;
        if (request.getThreadId() != null && !request.getThreadId().isBlank()) {
            thread = threadRepository.findById(request.getThreadId())
                    .orElseThrow(() -> new ResourceNotFoundException("MessageThread", "id", request.getThreadId()));
        } else if (request.getSubject() != null && !request.getSubject().isBlank()) {
            thread = threadRepository.findFirstBySubjectOrderByLastMessageAtDesc(request.getSubject())
                    .orElseGet(() -> {
                        List<String> participants = new ArrayList<>();
                        participants.add(senderId);
                        if (request.getRecipientIds() != null) {
                            participants.addAll(request.getRecipientIds());
                        }
                        return MessageThread.builder()
                                .subject(request.getSubject())
                                .participantIds(participants.stream().distinct().collect(Collectors.toList()))
                                .isGroup(participants.size() > 2)
                                .createdAt(Instant.now())
                                .build();
                    });
        } else {
            List<String> participants = new ArrayList<>();
            participants.add(senderId);
            if (request.getRecipientIds() != null) {
                participants.addAll(request.getRecipientIds());
            }

            thread = MessageThread.builder()
                    .subject("Direct Message")
                    .participantIds(participants.stream().distinct().collect(Collectors.toList()))
                    .isGroup(participants.size() > 2)
                    .createdAt(Instant.now())
                    .build();
        }

        thread.setLastMessage(request.getContent());
        thread.setLastMessageAt(Instant.now());
        thread.setUpdatedAt(Instant.now());
        MessageThread savedThread = threadRepository.save(thread);
        log.info("THREAD id={}, subject={}",
                savedThread.getId(),
                savedThread.getSubject());

        List<MessageAttachment> attachments = null;
        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            attachments = request.getAttachments().stream().map(a -> MessageAttachment.builder()
                    .fileName(a.getFileName())
                    .fileUrl(a.getFileUrl())
                    .fileType(a.getFileType())
                    .fileSize(a.getFileSize())
                    .uploadedAt(Instant.now())
                    .build()).collect(Collectors.toList());
        }

        Map<String, Instant> readBy = new HashMap<>();
        readBy.put(senderId, Instant.now());

        InternalMessage message = InternalMessage.builder()
                .threadId(savedThread.getId())
                .senderId(senderId)
                .senderName(senderName)
                .recipientIds(savedThread.getParticipantIds().stream().filter(p -> !p.equals(senderId)).collect(Collectors.toList()))
                .content(request.getContent())
                .attachments(attachments)
                .readBy(readBy)
                .sentAt(Instant.now())
                .build();

        InternalMessageDTO dto = mapToMessageDTO(messageRepository.save(message));
        try {
            String dest1 = "/topic/thread." + savedThread.getId();
            log.info("STOMP BROADCAST destination={}", dest1);
            log.info("Payload={}", dto);
            messagingTemplate.convertAndSend(dest1, dto);
            log.info("Broadcast completed.");

            if (request.getSubject() != null && !request.getSubject().isBlank()) {
                String dest2 = "/topic/project." + request.getSubject();
                log.info("STOMP BROADCAST destination={}", dest2);
                log.info("Payload={}", dto);
                messagingTemplate.convertAndSend(dest2, dto);
                log.info("Broadcast completed.");
            }

            String dest3 = "/topic/project." + savedThread.getId();
            log.info("STOMP BROADCAST destination={}", dest3);
            log.info("Payload={}", dto);
            messagingTemplate.convertAndSend(dest3, dto);
            log.info("Broadcast completed.");

            String dest4 = "/topic/global";
            log.info("STOMP BROADCAST destination={}", dest4);
            log.info("Payload={}", dto);
            messagingTemplate.convertAndSend(dest4, dto);
            log.info("Broadcast completed.");
        } catch (Exception e) {
            log.error("Broadcast error", e);
        }
        return dto;
    }

    @Override
    public MessageThreadDTO getThreadById(String threadId) {
        MessageThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new ResourceNotFoundException("MessageThread", "id", threadId));
        return mapToThreadDTO(thread);
    }

    @Override
    public List<MessageThreadDTO> getUserThreads(String userId) {
        return threadRepository.findByParticipantIdsContainingOrderByLastMessageAtDesc(userId)
                .stream()
                .map(this::mapToThreadDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InternalMessageDTO> getThreadMessages(String threadId, String userId) {
        return messageRepository.findByThreadIdOrderBySentAtAsc(threadId)
                .stream()
                .map(this::mapToMessageDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InternalMessageDTO> getProjectMessages(String projectCode) {
        Optional<MessageThread> threadOpt = threadRepository.findFirstBySubjectOrderByLastMessageAtDesc(projectCode);
        if (threadOpt.isEmpty()) {
            return new ArrayList<>();
        }
        return messageRepository.findByThreadIdOrderBySentAtAsc(threadOpt.get().getId())
                .stream()
                .map(this::mapToMessageDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InternalMessageDTO markMessageAsRead(String messageId, String userId) {
        InternalMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("InternalMessage", "id", messageId));

        Map<String, Instant> readBy = message.getReadBy() != null ? message.getReadBy() : new HashMap<>();
        readBy.put(userId, Instant.now());
        message.setReadBy(readBy);

        return mapToMessageDTO(messageRepository.save(message));
    }

    private MessageThreadDTO mapToThreadDTO(MessageThread t) {
        return MessageThreadDTO.builder()
                .id(t.getId())
                .subject(t.getSubject())
                .participantIds(t.getParticipantIds())
                .lastMessage(t.getLastMessage())
                .lastMessageAt(t.getLastMessageAt())
                .isGroup(t.isGroup())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .createdBy(t.getCreatedBy())
                .build();
    }

    private InternalMessageDTO mapToMessageDTO(InternalMessage m) {
        List<MessageAttachmentDTO> attDtos = null;
        if (m.getAttachments() != null) {
            attDtos = m.getAttachments().stream().map(a -> MessageAttachmentDTO.builder()
                    .fileName(a.getFileName())
                    .fileUrl(a.getFileUrl())
                    .fileType(a.getFileType())
                    .fileSize(a.getFileSize())
                    .uploadedAt(a.getUploadedAt())
                    .build()).collect(Collectors.toList());
        }

        return InternalMessageDTO.builder()
                .id(m.getId())
                .threadId(m.getThreadId())
                .senderId(m.getSenderId())
                .senderName(m.getSenderName())
                .recipientIds(m.getRecipientIds())
                .content(m.getContent())
                .attachments(attDtos)
                .readBy(m.getReadBy())
                .sentAt(m.getSentAt())
                .createdBy(m.getCreatedBy())
                .build();
    }
}
