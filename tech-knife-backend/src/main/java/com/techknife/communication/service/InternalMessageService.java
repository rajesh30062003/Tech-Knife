package com.techknife.communication.service;

import com.techknife.communication.dto.InternalMessageDTO;
import com.techknife.communication.dto.MessageThreadDTO;
import com.techknife.communication.dto.SendMessageRequest;

import java.util.List;

public interface InternalMessageService {

    InternalMessageDTO sendMessage(SendMessageRequest request, String senderId, String senderName);

    MessageThreadDTO getThreadById(String threadId);

    List<MessageThreadDTO> getUserThreads(String userId);

    List<InternalMessageDTO> getThreadMessages(String threadId, String userId);

    List<InternalMessageDTO> getProjectMessages(String projectCode);

    InternalMessageDTO markMessageAsRead(String messageId, String userId);
}
