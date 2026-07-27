package com.techknife.communication.service.impl;

import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.communication.dto.*;
import com.techknife.communication.entity.*;
import com.techknife.communication.repository.*;
import com.techknife.communication.service.InternalMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InternalMessageServiceImpl implements InternalMessageService {

    private final InternalMessageRepository messageRepository;
    private final MessageThreadRepository threadRepository;

    @Override
    public InternalMessageDTO sendMessage(SendMessageRequest request, String senderId, String senderName) {
        MessageThread thread;
        if (request.getThreadId() != null && !request.getThreadId().isBlank()) {
            thread = threadRepository.findById(request.getThreadId())
                    .orElseThrow(() -> new ResourceNotFoundException("MessageThread", "id", request.getThreadId()));
        } else {
            List<String> participants = new ArrayList<>();
            participants.add(senderId);
            if (request.getRecipientIds() != null) {
                participants.addAll(request.getRecipientIds());
            }

            thread = MessageThread.builder()
                    .subject(request.getSubject() != null ? request.getSubject() : "Direct Message")
                    .participantIds(participants.stream().distinct().collect(Collectors.toList()))
                    .isGroup(participants.size() > 2)
                    .createdAt(Instant.now())
                    .build();
        }

        thread.setLastMessage(request.getContent());
        thread.setLastMessageAt(Instant.now());
        thread.setUpdatedAt(Instant.now());
        MessageThread savedThread = threadRepository.save(thread);

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

        return mapToMessageDTO(messageRepository.save(message));
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
