package com.techknife.finance.service.impl;

import com.techknife.finance.dto.ExpenseDTO;
import com.techknife.finance.dto.FinanceDashboardDTO;
import com.techknife.finance.dto.InvoiceDTO;
import com.techknife.finance.dto.JournalEntryDTO;
import com.techknife.finance.entity.Budget;
import com.techknife.finance.entity.Expense;
import com.techknife.finance.entity.Invoice;
import com.techknife.finance.entity.JournalEntry;
import com.techknife.finance.repository.BudgetRepository;
import com.techknife.finance.repository.ExpenseRepository;
import com.techknife.finance.repository.InvoiceRepository;
import com.techknife.finance.repository.JournalEntryRepository;
import com.techknife.finance.service.FinanceDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceDashboardServiceImpl implements FinanceDashboardService {

    private final InvoiceRepository invoiceRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final JournalEntryRepository journalEntryRepository;

    @Override
    public FinanceDashboardDTO getDashboardData() {
        List<Invoice> invoices = invoiceRepository.findAll();
        List<Expense> expenses = expenseRepository.findAll();
        List<Budget> budgets = budgetRepository.findAll();
        List<JournalEntry> journals = journalEntryRepository.findAll();

        BigDecimal totalRevenue = invoices.stream()
                .map(i -> i.getTotalAmount() != null ? i.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = expenses.stream()
                .map(e -> e.getAmount() != null ? e.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netProfit = totalRevenue.subtract(totalExpenses);

        List<Invoice> outstandingInvoices = invoices.stream()
                .filter(i -> i.getBalanceDue() != null && i.getBalanceDue().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        BigDecimal outstandingAmount = outstandingInvoices.stream()
                .map(Invoice::getBalanceDue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalBudgeted = budgets.stream()
                .map(b -> b.getBudgetedAmount() != null ? b.getBudgetedAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalActualBudget = budgets.stream()
                .map(b -> b.getActualAmount() != null ? b.getActualAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double utilizationPct = 0.0;
        if (totalBudgeted.compareTo(BigDecimal.ZERO) > 0) {
            utilizationPct = totalActualBudget.divide(totalBudgeted, 4, RoundingMode.HALF_UP).doubleValue() * 100;
        }

        List<InvoiceDTO> recentInvoices = invoices.stream().limit(5)
                .map(i -> InvoiceDTO.builder()
                        .id(i.getId())
                        .invoiceNumber(i.getInvoiceNumber())
                        .customerName(i.getCustomerName())
                        .totalAmount(i.getTotalAmount())
                        .status(i.getStatus())
                        .issueDate(i.getIssueDate())
                        .build()).collect(Collectors.toList());

        List<ExpenseDTO> recentExpenses = expenses.stream().limit(5)
                .map(e -> ExpenseDTO.builder()
                        .id(e.getId())
                        .expenseNumber(e.getExpenseNumber())
                        .title(e.getTitle())
                        .amount(e.getAmount())
                        .approvalStatus(e.getApprovalStatus())
                        .expenseDate(e.getExpenseDate())
                        .build()).collect(Collectors.toList());

        List<JournalEntryDTO> recentJournals = journals.stream().limit(5)
                .map(j -> JournalEntryDTO.builder()
                        .id(j.getId())
                        .journalNumber(j.getJournalNumber())
                        .narration(j.getNarration())
                        .totalDebit(j.getTotalDebit())
                        .status(j.getStatus())
                        .entryDate(j.getEntryDate())
                        .build()).collect(Collectors.toList());

        return FinanceDashboardDTO.builder()
                .totalRevenue(totalRevenue)
                .totalExpenses(totalExpenses)
                .netProfit(netProfit)
                .outstandingInvoicesAmount(outstandingAmount)
                .outstandingInvoicesCount((long) outstandingInvoices.size())
                .cashPosition(totalRevenue.subtract(totalExpenses))
                .budgetUtilizationPercentage(utilizationPct)
                .recentInvoices(recentInvoices)
                .recentExpenses(recentExpenses)
                .recentJournalEntries(recentJournals)
                .build();
    }
}
