package com.techknife.customerportal.service.impl;

import com.techknife.customerportal.dto.*;
import com.techknife.customerportal.entity.CustomerProject;
import com.techknife.customerportal.entity.InvoiceView;
import com.techknife.customerportal.entity.PaymentHistory;
import com.techknife.customerportal.entity.SupportTicket;
import com.techknife.customerportal.repository.*;
import com.techknife.customerportal.service.CustomerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerDashboardServiceImpl implements CustomerDashboardService {

    private final CustomerProjectRepository customerProjectRepository;
    private final CustomerMilestoneRepository customerMilestoneRepository;
    private final CustomerTaskViewRepository customerTaskViewRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final InvoiceViewRepository invoiceViewRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final CustomerNotificationRepository customerNotificationRepository;
    private final CustomerFeedbackRepository customerFeedbackRepository;

    @Override
    public CustomerDashboardDTO getDashboard(String customerAccountId) {
        long totalProjects = customerProjectRepository.countByCustomerAccountId(customerAccountId);
        long activeProjects = customerProjectRepository.countByCustomerAccountIdAndStatus(customerAccountId, "IN_PROGRESS");
        long completedProjects = customerProjectRepository.countByCustomerAccountIdAndStatus(customerAccountId, "COMPLETED");
        long pendingTasks = customerTaskViewRepository.countByCustomerAccountIdAndStatus(customerAccountId, "PENDING");
        long openTickets = supportTicketRepository.countByCustomerAccountIdAndStatus(customerAccountId, "OPEN");
        long totalInvoices = invoiceViewRepository.countByCustomerAccountId(customerAccountId);
        long unpaidInvoices = invoiceViewRepository.countByCustomerAccountIdAndStatus(customerAccountId, "UNPAID");

        List<InvoiceView> unpaidList = invoiceViewRepository.findByCustomerAccountIdAndStatus(customerAccountId, "UNPAID");
        double totalOutstandingAmount = unpaidList.stream().mapToDouble(i -> i.getTotalAmount() != null ? i.getTotalAmount() : 0.0).sum();

        List<PaymentHistory> payments = paymentHistoryRepository.findByCustomerAccountId(customerAccountId);
        long totalPayments = payments.size();
        double totalPaidAmount = payments.stream().mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0).sum();

        long unreadNotifications = customerNotificationRepository.countByCustomerAccountIdAndIsReadFalse(customerAccountId);

        List<CustomerProjectDTO> recentProjects = customerProjectRepository.findByCustomerAccountId(customerAccountId).stream()
                .limit(5)
                .map(p -> CustomerProjectDTO.builder()
                        .id(p.getId())
                        .projectCode(p.getProjectCode())
                        .projectName(p.getProjectName())
                        .status(p.getStatus())
                        .progressPercentage(p.getProgressPercentage())
                        .startDate(p.getStartDate())
                        .endDate(p.getEndDate())
                        .build())
                .collect(Collectors.toList());

        List<CustomerMilestoneDTO> upcomingMilestones = customerMilestoneRepository.findByCustomerAccountId(customerAccountId).stream()
                .filter(m -> !"COMPLETED".equalsIgnoreCase(m.getStatus()))
                .limit(5)
                .map(m -> CustomerMilestoneDTO.builder()
                        .id(m.getId())
                        .projectId(m.getProjectId())
                        .milestoneName(m.getMilestoneName())
                        .status(m.getStatus())
                        .dueDate(m.getDueDate())
                        .completionPercentage(m.getCompletionPercentage())
                        .build())
                .collect(Collectors.toList());

        List<SupportTicketDTO> recentTickets = supportTicketRepository.findByCustomerAccountId(customerAccountId).stream()
                .limit(5)
                .map(t -> SupportTicketDTO.builder()
                        .id(t.getId())
                        .ticketNumber(t.getTicketNumber())
                        .title(t.getTitle())
                        .priority(t.getPriority())
                        .status(t.getStatus())
                        .createdAt(t.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        List<InvoiceViewDTO> recentInvoices = invoiceViewRepository.findByCustomerAccountId(customerAccountId).stream()
                .limit(5)
                .map(i -> InvoiceViewDTO.builder()
                        .id(i.getId())
                        .invoiceNumber(i.getInvoiceNumber())
                        .totalAmount(i.getTotalAmount())
                        .status(i.getStatus())
                        .dueDate(i.getDueDate())
                        .build())
                .collect(Collectors.toList());

        List<CustomerNotificationDTO> recentNotifications = customerNotificationRepository.findByCustomerAccountIdOrderByCreatedAtDesc(customerAccountId).stream()
                .limit(5)
                .map(n -> CustomerNotificationDTO.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .message(n.getMessage())
                        .type(n.getType())
                        .isRead(n.getIsRead())
                        .createdAt(n.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return CustomerDashboardDTO.builder()
                .totalProjects(totalProjects)
                .activeProjects(activeProjects)
                .completedProjects(completedProjects)
                .pendingTasks(pendingTasks)
                .openTickets(openTickets)
                .totalInvoices(totalInvoices)
                .unpaidInvoices(unpaidInvoices)
                .totalOutstandingAmount(totalOutstandingAmount)
                .totalPayments(totalPayments)
                .totalPaidAmount(totalPaidAmount)
                .unreadNotifications(unreadNotifications)
                .recentProjects(recentProjects)
                .upcomingMilestones(upcomingMilestones)
                .recentTickets(recentTickets)
                .recentInvoices(recentInvoices)
                .recentNotifications(recentNotifications)
                .build();
    }

    @Override
    public CustomerAnalyticsDTO getAnalytics(String customerAccountId) {
        List<CustomerProject> projects = customerProjectRepository.findByCustomerAccountId(customerAccountId);
        long totalProj = projects.size();
        long completedProj = projects.stream().filter(p -> "COMPLETED".equalsIgnoreCase(p.getStatus())).count();
        double completionRate = totalProj > 0 ? ((double) completedProj / totalProj) * 100.0 : 0.0;

        List<SupportTicket> tickets = supportTicketRepository.findByCustomerAccountId(customerAccountId);
        long openIssues = tickets.stream().filter(t -> "OPEN".equalsIgnoreCase(t.getStatus()) || "IN_PROGRESS".equalsIgnoreCase(t.getStatus())).count();

        double totalResolutionHours = 0.0;
        int resolvedCount = 0;
        for (SupportTicket ticket : tickets) {
            if (ticket.getClosedAt() != null && ticket.getCreatedAt() != null) {
                totalResolutionHours += Duration.between(ticket.getCreatedAt(), ticket.getClosedAt()).toHours();
                resolvedCount++;
            }
        }
        double avgResolutionTime = resolvedCount > 0 ? totalResolutionHours / resolvedCount : 24.0;

        var feedbacks = customerFeedbackRepository.findByCustomerAccountId(customerAccountId);
        double avgSatisfaction = feedbacks.isEmpty() ? 9.0 : feedbacks.stream()
                .mapToDouble(f -> f.getSatisfactionScore() != null ? f.getSatisfactionScore() : 8.0)
                .average().orElse(9.0);

        List<InvoiceView> invoices = invoiceViewRepository.findByCustomerAccountId(customerAccountId);
        Map<String, Long> invoiceStatusMap = invoices.stream()
                .collect(Collectors.groupingBy(i -> i.getStatus() != null ? i.getStatus() : "UNPAID", Collectors.counting()));

        Map<String, Long> projectStatusMap = projects.stream()
                .collect(Collectors.groupingBy(p -> p.getStatus() != null ? p.getStatus() : "IN_PROGRESS", Collectors.counting()));

        Map<String, Long> ticketPriorityMap = tickets.stream()
                .collect(Collectors.groupingBy(t -> t.getPriority() != null ? t.getPriority() : "MEDIUM", Collectors.counting()));

        return CustomerAnalyticsDTO.builder()
                .projectCompletionRate(Math.round(completionRate * 10.0) / 10.0)
                .avgTicketResolutionTimeHours(Math.round(avgResolutionTime * 10.0) / 10.0)
                .customerSatisfactionScore(Math.round(avgSatisfaction * 10.0) / 10.0)
                .openIssuesCount(openIssues)
                .invoiceStatusBreakdown(invoiceStatusMap)
                .projectStatusBreakdown(projectStatusMap)
                .ticketPriorityBreakdown(ticketPriorityMap)
                .build();
    }
}
