package com.chxt.domain.transaction.model.entity;

import com.alibaba.fastjson2.JSON;
import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.transaction.model.vo.TransactionTagVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class TransactionDashboard {

    private final List<Transaction> transactions;

    public TransactionDashboard(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    /**
     * 天维度统计 key是日期格式 yyyy-MM-dd
     */
    public List<DashboardMetrics> getDay() {
        Map<String, List<Transaction>> dayTransactionMap = this.getTransactionsOrEmpty().stream()
                .filter(this::hasDateAndAmount)
                .collect(Collectors.groupingBy(
                        item -> DateFormatUtils.format(item.getDate(), "yyyy-MM-dd"),
                        TreeMap::new,
                        Collectors.toList()
                ));
        return dayTransactionMap.entrySet().stream()
                .map(item -> this.buildDashboardMetrics(item.getValue(), item.getKey(), null))
                .collect(Collectors.toList());
    }

    /**
     * 月维度统计 key是 yyyy-MM
     */
    public List<DashboardMetrics> getMonth() {
        Map<String, List<Transaction>> monthTransactionMap = this.getTransactionsOrEmpty().stream()
                .filter(this::hasDateAndAmount)
                .collect(Collectors.groupingBy(
                        item -> DateFormatUtils.format(item.getDate(), "yyyy-MM"),
                        TreeMap::new,
                        Collectors.toList()
                ));
        return monthTransactionMap.entrySet().stream()
                .map(item -> this.buildDashboardMetrics(item.getValue(), item.getKey(), null))
                .collect(Collectors.toList());
    }

    /**
     * 标签维度统计
     */
    public List<DashboardMetrics> getTag() {
        Map<String, List<Transaction>> tagTransactionMap = this.getTransactionsOrEmpty().stream()
                .filter(Objects::nonNull)
                .filter(this::hasAmount)
                .flatMap(transaction -> this.getTagsOrEmpty(transaction).stream()
                        .map(tag -> new AbstractMap.SimpleEntry<>(tag, transaction)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        TreeMap::new,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

        return tagTransactionMap.entrySet().stream()
                .sorted((left, right) -> {
                    int compareCount = Long.compare(
                            right.getValue().size(),
                            left.getValue().size()
                    );
                    if (compareCount != 0) {
                        return compareCount;
                    }
                    return left.getKey().compareTo(right.getKey());
                })
                .map(item -> this.buildDashboardMetrics(item.getValue(), null, item.getKey()))
                .collect(Collectors.toList());
    }

    private List<Transaction> getTransactionsOrEmpty() {
        return this.transactions == null ? Collections.emptyList() : this.transactions;
    }

    private Set<String> getTagsOrEmpty(Transaction transaction) {
        if (transaction == null || transaction.getTags() == null) {
            return Collections.emptySet();
        }
        return transaction.getTags().stream()
                .map(TransactionTagVO::getTag)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private DashboardMetrics buildDashboardMetrics(List<Transaction> transactionList, String date, String tag) {
        BigDecimal expense = BigDecimal.ZERO;
        long expenseCount = 0L;
        BigDecimal income = BigDecimal.ZERO;
        long incomeCount = 0L;

        for (Transaction transaction : transactionList) {
            if (this.isExpense(transaction)) {
                expense = expense.add(transaction.getAmount());
                expenseCount++;
                continue;
            }
            if (this.isIncome(transaction)) {
                income = income.add(transaction.getAmount());
                incomeCount++;
            }
        }

        return DashboardMetrics.builder()
                .date(date)
                .tag(tag)
                .expense(expense.toPlainString())
                .expenseCount(expenseCount)
                .income(income.toPlainString())
                .incomeCount(incomeCount)
                .build();
    }

    private boolean hasDateAndAmount(Transaction transaction) {
        return transaction != null && transaction.getDate() != null && transaction.getAmount() != null;
    }

    private boolean hasAmount(Transaction transaction) {
        return transaction != null && transaction.getAmount() != null;
    }

    private boolean isExpense(Transaction transaction) {
        return transaction != null
                && TransactionEnums.Type.EXPENSE.getCode().equals(transaction.getType());
    }

    private boolean isIncome(Transaction transaction) {
        return transaction != null
                && TransactionEnums.Type.INCOME.getCode().equals(transaction.getType());
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardMetrics {
        private String date;
        private String tag;
        private String expense;
        private Long expenseCount;
        private String income;
        private Long incomeCount;
    }
}
