package com.chxt.domain.transaction.model.entity;

import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.transaction.model.vo.TransactionTagVO;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionDashboard {

    private final List<Transaction> transactions;



    public TransactionDashboard(List<Transaction> transactions) {
        this.transactions = transactions;
        Map<String, String> dayExpense = this.getDayExpense();
        Map<String, String> monthExpense = this.getMonthExpense();
        Map<String, String> tagCount = this.getTagCount();
    }

    /**
     * 天维度支出 key是日期格式yyyy-MM-dd
     */
    public Map<String, String> getDayExpense() {
        Map<String, BigDecimal> dayExpense = this.getTransactionsOrEmpty().stream()
                .filter(this::isExpense)
                .filter(item -> item.getDate() != null && item.getAmount() != null)
                .collect(Collectors.groupingBy(
                        item -> DateFormatUtils.format(item.getDate(), "yyyy-MM-dd"),
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
        return this.toStringMap(dayExpense);
    }

    /**
     * 月维度支出 key是 月份的数字
     */
    public Map<String, String> getMonthExpense() {
        Map<String, BigDecimal> monthExpense = this.getTransactionsOrEmpty().stream()
                .filter(this::isExpense)
                .filter(item -> item.getDate() != null && item.getAmount() != null)
                .collect(Collectors.groupingBy(
                        item -> DateFormatUtils.format(item.getDate(), "yyyy-MM"),
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
        return this.toStringMap(monthExpense);
    }

    /**
     * 统计每个tag下存在transaction
     */
    public Map<String, String> getTagCount() {
        Map<String, Long> tagCount = this.getTransactionsOrEmpty().stream()
                .map(Transaction::getTags)
                .filter(Objects::nonNull)
                .map(tags -> tags.stream()
                        .map(TransactionTagVO::getTag)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet()))
                .flatMap(Set::stream)
                .collect(Collectors.groupingBy(
                        item -> item,
                        TreeMap::new,
                        Collectors.counting()
                ));
        return tagCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        item -> String.valueOf(item.getValue()),
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }

    private List<Transaction> getTransactionsOrEmpty() {
        return this.transactions == null ? Collections.emptyList() : this.transactions;
    }

    private boolean isExpense(Transaction transaction) {
        return transaction != null
                && TransactionEnums.Type.EXPENSE.getCode().equals(transaction.getType());
    }

    private Map<String, String> toStringMap(Map<String, BigDecimal> source) {
        return source.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        item -> item.getValue().toPlainString(),
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }

}
