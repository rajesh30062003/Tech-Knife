package com.techknife.customerportal.service;

import com.techknife.customerportal.dto.SupportTicketDTO;
import com.techknife.customerportal.dto.TicketReplyDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SupportTicketService {

    List<SupportTicketDTO> getTickets(String customerAccountId, String status);

    SupportTicketDTO getTicketById(String id, String customerAccountId);

    SupportTicketDTO createTicket(String customerAccountId, SupportTicketDTO dto, MultipartFile[] files);

    SupportTicketDTO addReply(String ticketId, String customerAccountId, TicketReplyDTO replyDTO, MultipartFile[] files);

    SupportTicketDTO updateTicketStatus(String ticketId, String customerAccountId, String status, String resolutionNotes);

    SupportTicketDTO closeTicket(String ticketId, String customerAccountId);

    SupportTicketDTO reopenTicket(String ticketId, String customerAccountId);
}
