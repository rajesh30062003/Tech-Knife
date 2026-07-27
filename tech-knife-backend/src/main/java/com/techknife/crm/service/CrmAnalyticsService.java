package com.techknife.crm.service;

import com.techknife.crm.dto.CrmAnalyticsDTO;
import com.techknife.crm.entity.Customer;
import com.techknife.crm.entity.Lead;
import com.techknife.crm.entity.LeadStatus;
import com.techknife.crm.entity.Opportunity;
import com.techknife.crm.entity.SalesStage;
import com.techknife.crm.repository.CustomerRepository;
import com.techknife.crm.repository.LeadRepository;
import com.techknife.crm.repository.OpportunityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrmAnalyticsService {

    private final LeadRepository leadRepository;
    private final OpportunityRepository opportunityRepository;
    private final CustomerRepository customerRepository;

    public CrmAnalyticsDTO getAnalyticsData() {
        List<Lead> leads = leadRepository.findAll();
        List<Opportunity> opportunities = opportunityRepository.findAll();
        List<Customer> customers = customerRepository.findAll();

        long totalLeads = leads.size();
        long convertedLeads = leads.stream()
                .filter(l -> l.getLeadStatus() == LeadStatus.CONVERTED || l.getLeadStatus() == LeadStatus.QUALIFIED)
                .count();

        double leadConversionRate = totalLeads > 0 ? ((double) convertedLeads / totalLeads) * 100.0 : 0.0;

        Map<String, Double> salesPerformance = new HashMap<>();
        double totalWonRevenue = 0.0;
        long wonCount = 0;
        double totalSalesCycleDays = 0.0;

        for (Opportunity opp : opportunities) {
            if (opp.getSalesStage() == SalesStage.WON || "WON".equalsIgnoreCase(opp.getStatus())) {
                wonCount++;
                double rev = opp.getEstimatedRevenue() != null ? opp.getEstimatedRevenue() : 0.0;
                totalWonRevenue += rev;

                String mgr = opp.getAssignedEmployeeId() != null ? opp.getAssignedEmployeeId() : "UNASSIGNED";
                salesPerformance.put(mgr, salesPerformance.getOrDefault(mgr, 0.0) + rev);

                if (opp.getCreatedAt() != null && opp.getUpdatedAt() != null) {
                    long days = Duration.between(opp.getCreatedAt(), opp.getUpdatedAt()).toDays();
                    totalSalesCycleDays += Math.max(days, 1);
                }
            }
        }

        double avgDealSize = wonCount > 0 ? totalWonRevenue / wonCount : 0.0;
        double avgSalesCycleDays = wonCount > 0 ? totalSalesCycleDays / wonCount : 0.0;

        Map<String, Long> topSourcesCount = new HashMap<>();
        Map<String, Long> topSourcesConverted = new HashMap<>();

        for (Lead lead : leads) {
            String src = lead.getLeadSource() != null ? lead.getLeadSource().name() : "UNKNOWN";
            topSourcesCount.put(src, topSourcesCount.getOrDefault(src, 0L) + 1);
            if (lead.getLeadStatus() == LeadStatus.CONVERTED) {
                topSourcesConverted.put(src, topSourcesConverted.getOrDefault(src, 0L) + 1);
            }
        }

        Map<String, Double> topSourcesConversion = new HashMap<>();
        for (String src : topSourcesCount.keySet()) {
            long total = topSourcesCount.get(src);
            long conv = topSourcesConverted.getOrDefault(src, 0L);
            topSourcesConversion.put(src, total > 0 ? ((double) conv / total) * 100.0 : 0.0);
        }

        Map<String, Long> customerAcquisitionTrend = new HashMap<>();
        DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyy-MM").withZone(ZoneId.systemDefault());

        for (Customer customer : customers) {
            if (customer.getCreatedAt() != null) {
                String yearMonth = yearMonthFormatter.format(customer.getCreatedAt());
                customerAcquisitionTrend.put(yearMonth, customerAcquisitionTrend.getOrDefault(yearMonth, 0L) + 1);
            }
        }

        return CrmAnalyticsDTO.builder()
                .leadConversionRate(leadConversionRate)
                .salesPerformanceByAccountManager(salesPerformance)
                .topLeadSourcesCount(topSourcesCount)
                .topLeadSourcesConversionRate(topSourcesConversion)
                .averageDealSize(avgDealSize)
                .averageSalesCycleDays(avgSalesCycleDays)
                .customerAcquisitionTrendMonthly(customerAcquisitionTrend)
                .build();
    }
}
