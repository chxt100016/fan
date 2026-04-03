package com.chxt.domain.obsidian;

import com.chxt.domain.transaction.model.entity.TransactionDashboard;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ListFormat {


    public Map<String, List<String>> data;

    public static ListFormat of(String name, TransactionDashboard dashboard) {
        ListFormat format = new ListFormat();
        Map<String, List<String>> result = new LinkedHashMap<>();

        String safeName = (name == null || name.isBlank()) ? "dashboard" : name;
        if (dashboard != null) {
            result.put(safeName + "-day.md", buildLines(dashboard.getDay()));
            result.put(safeName + "-month.md", buildLines(dashboard.getMonth()));
            result.put(safeName + "-tag.md", buildLines(dashboard.getTag()));
        }

        format.data = result;
        return format;
    }

    private static List<String> buildLines(List<TransactionDashboard.DashboardMetrics> metricsList) {
        if (metricsList == null || metricsList.isEmpty()) {
            return List.of();
        }
        List<String> lines = new ArrayList<>(metricsList.size());
        for (TransactionDashboard.DashboardMetrics metrics : metricsList) {
            lines.add("- " + buildTags(metrics));
        }
        return lines;
    }

    private static String buildTags(TransactionDashboard.DashboardMetrics metrics) {
        if (metrics == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        appendTag(builder, "date", metrics.getDate());
        appendTag(builder, "tag", metrics.getTag());
        appendTag(builder, "expense", metrics.getExpense());
        appendTag(builder, "expenseCount", metrics.getExpenseCount());
        appendTag(builder, "income", metrics.getIncome());
        appendTag(builder, "incomeCount", metrics.getIncomeCount());
        return builder.toString();
    }

    private static void appendTag(StringBuilder builder, String key, Object value) {
        if (Objects.isNull(value)) {
            return;
        }
        builder.append("[").append(key).append("::").append(value).append("]");
    }
}
