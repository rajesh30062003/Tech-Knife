package com.techknife.crm.service;

import com.techknife.crm.dto.CrmDashboardDTO;
import com.techknife.crm.dto.CustomerDTO;
import com.techknife.crm.dto.FollowUpDTO;
import com.techknife.crm.entity.Customer;
import com.techknife.crm.entity.FollowUp;
import com.techknife.crm.entity.Lead;
import com.techknife.crm.entity.Opportunity;
import com.techknife.crm.entity.SalesStage;
import com.techknife.crm.repository.CustomerRepository;
import com.techknife.crm.repository.FollowUpRepository;
import com.techknife.crm.repository.LeadRepository;
import com.techknife.crm.repository.OpportunityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrmDashboardService {

    private final LeadRepository leadRepository;
    private final OpportunityRepository opportunityRepository;
    private final CustomerRepository customerRepository;
    private final FollowUpRepository followUpRepository;
    private final CustomerService customerService;
    private final FollowUpService followUpService;

    public CrmDashboardDTO getDashboardData() {
        List<Lead> leads = leadRepository.findAll();
        List<Opportunity> opportunities = opportunityRepository.findAll();
        List<Customer> customers = customerRepository.findAll();
        List<FollowUp> followUps = followUpRepository.findByStatus("PENDING");

        long totalLeads = leads.size();

        Map<String, Long> leadsByStatus = new HashMap<>();
        for (Lead lead : leads) {
            String status = lead.getLeadStatus() != null ? lead.getLeadStatus().name() : "UNKNOWN";
            leadsByStatus.put(status, leadsByStatus.getOrDefault(status, 0L) + 1);
        }

        Map<String, Long> leadsBySource = new HashMap<>();
        for (Lead lead : leads) {
            String source = lead.getLeadSource() != null ? lead.getLeadSource().name() : "UNKNOWN";
            leadsBySource.put(source, leadsBySource.getOrDefault(source, 0L) + 1);
        }

        Map<String, Long> salesFunnelCount = new HashMap<>();
        Map<String, Double> salesFunnelValue = new HashMap<>();

        for (SalesStage stage : SalesStage.values()) {
            salesFunnelCount.put(stage.name(), 0L);
            salesFunnelValue.put(stage.name(), 0.0);
        }

        double totalPipelineValue = 0.0;
        long wonDealsCount = 0;
        double wonDealsValue = 0.0;
        long lostDealsCount = 0;
        double lostDealsValue = 0.0;
        double revenueForecast = 0.0;

        for (Opportunity opp : opportunities) {
            String stageStr = opp.getSalesStage() != null ? opp.getSalesStage().name() : SalesStage.LEAD.name();
            double rev = opp.getEstimatedRevenue() != null ? opp.getEstimatedRevenue() : 0.0;
            double prob = opp.getProbabilityPercentage() != null ? opp.getProbabilityPercentage() : 50.0;

            salesFunnelCount.put(stageStr, salesFunnelCount.getOrDefault(stageStr, 0L) + 1);
            salesFunnelValue.put(stageStr, salesFunnelValue.getOrDefault(stageStr, 0.0) + rev);

            if (opp.getSalesStage() == SalesStage.WON || "WON".equalsIgnoreCase(opp.getStatus())) {
                wonDealsCount++;
                wonDealsValue += rev;
            } else if (opp.getSalesStage() == SalesStage.LOST || "LOST".equalsIgnoreCase(opp.getStatus())) {
                lostDealsCount++;
                lostDealsValue += rev;
            } else {
                totalPipelineValue += rev;
                revenueForecast += (rev * (prob / 100.0));
            }
        }

        List<FollowUpDTO> upcomingFollowUps = followUps.stream()
                .limit(10)
                .map(followUpService::mapToDTO)
                .collect(Collectors.toList());

        List<CustomerDTO> recentCustomers = customers.stream()
                .limit(10)
                .map(customerService::mapToDTO)
                .collect(Collectors.toList());

        return CrmDashboardDTO.builder()
                .totalLeads(totalLeads)
                .leadsByStatus(leadsByStatus)
                .leadsBySource(leadsBySource)
                .salesFunnelCountByStage(salesFunnelCount)
                .salesFunnelValueByStage(salesFunnelValue)
                .totalPipelineValue(totalPipelineValue)
                .wonDealsCount(wonDealsCount)
                .wonDealsValue(wonDealsValue)
                .lostDealsCount(lostDealsCount)
                .lostDealsValue(lostDealsValue)
                .upcomingFollowUps(upcomingFollowUps)
                .recentCustomers(recentCustomers)
                .revenueForecast(revenueForecast)
                .build();
    }
}
