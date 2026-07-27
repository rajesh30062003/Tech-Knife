package com.techknife.customerportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDashboardDTO {

    private long totalProjects;
    private long activeProjects;
    private long completedProjects;
    private long pendingTasks;
    private long openTickets;
    private long totalInvoices;
    private long unpaidInvoices;
    private double totalOutstandingAmount;
    private long totalPayments;
    private double totalPaidAmount;
    private long unreadNotifications;

    private List<CustomerProjectDTO> recentProjects;
    private List<CustomerMilestoneDTO> upcomingMilestones;
    private List<SupportTicketDTO> recentTickets;
    private List<InvoiceViewDTO> recentInvoices;
    private List<CustomerNotificationDTO> recentNotifications;
}
