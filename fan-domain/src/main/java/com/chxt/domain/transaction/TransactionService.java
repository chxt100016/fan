package com.chxt.domain.transaction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.chxt.domain.transaction.model.entity.Transaction;
import com.chxt.domain.transaction.model.entity.TransactionChannel;
import com.chxt.domain.transaction.model.entity.TransactionLog;
import com.chxt.domain.transaction.model.vo.MailParseParamVO;
import com.chxt.domain.transaction.parser.MailManager;
import com.chxt.domain.transaction.parser.PasswordHelper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransactionService {
    
    /**
     * 合并交易记录的时间窗口（毫秒）
     */
    private static final long MERGE_TIME_WINDOW_MS = 10 * 1000;

    @SneakyThrows
    public static List<TransactionChannel> init(MailParseParamVO param, PasswordHelper passwordHelper) {
        log.info("开始解析交易记录");


        // 注册策略
        MailManager manager = MailManager.Builder()
            .setHost(param.getHost())
            .setUsername(param.getUsername())
            .setPassword(param.getPassword())
			.setPasswordHelper(passwordHelper)
			.addStrategy(param.getParserCode())
            .build();

        // 使用策略管理器处理邮件
        List<TransactionChannel> list = manager.parse(param.getStartDateStr());

        // 输出所有解析结果
        return list;
    }

    /**
     * 合并来自不同渠道的交易记录
     * @param channels 包含多个渠道交易记录的列表
     * @return 合并后的交易列表
     */
    public static List<Transaction> merge(List<TransactionChannel> channels) {
        log.info("开始合并交易记录");
        // 将所有渠道的交易日志展平到一个列表中
        List<TransactionLog> allLogs = channels.stream()
                .flatMap(channel -> channel.getLogs().stream())
                .collect(Collectors.toList());

        List<Transaction> mergedTransactions = new ArrayList<>();
        // 用于跟踪已处理的日志ID，避免重复处理
        Set<String> processedLogIds = new HashSet<>();

        // 遍历所有日志以寻找匹配项
        for (int i = 0; i < allLogs.size(); i++) {
            TransactionLog log1 = allLogs.get(i);
            // 如果日志已被处理，则跳过
            if (processedLogIds.contains(log1.getLogId())) {
                continue;
            }

            // 潜在的匹配日志列表，初始包含当前日志
            List<TransactionLog> potentialMatches = new ArrayList<>();
            potentialMatches.add(log1);

            // 从当前日志的下一个开始，继续遍历以寻找匹配
            for (int j = i + 1; j < allLogs.size(); j++) {
                TransactionLog log2 = allLogs.get(j);
                // 如果日志已被处理，则跳过
                if (processedLogIds.contains(log2.getLogId())) {
                    continue;
                }

                // 检查金额是否相同，渠道是否不同，以及时间是否在指定窗口内
                if (log1.getAmount().compareTo(log2.getAmount()) == 0 &&
                    !log1.getChannel().equals(log2.getChannel()) &&
                    Math.abs(log1.getDate().getTime() - log2.getDate().getTime()) <= MERGE_TIME_WINDOW_MS) {
                    
                    boolean compatible = true;
                    // 确保待匹配的日志log2与已匹配的日志列表中的渠道不重复
                    for (TransactionLog matchedLog : potentialMatches) {
                        if (matchedLog.getChannel().equals(log2.getChannel())) {
                            compatible = false;
                            break;
                        }
                    }

                    // 如果渠道不重复，则添加为匹配项
                    if (compatible) {
                        potentialMatches.add(log2);
                    }
                }
            }

            // 如果找到了多个匹配项（即包含原始日志在内的多于一个日志）
            if (potentialMatches.size() > 1) {
                // 将所有匹配的日志ID标记为已处理
                for (TransactionLog log : potentialMatches) {
                    processedLogIds.add(log.getLogId());
                }

                // 按日期对匹配的日志进行排序
                potentialMatches.sort(Comparator.comparing(TransactionLog::getDate));

                // 使用第一个日志的信息创建合并后的交易对象
                TransactionLog firstLog = potentialMatches.get(0);
                Transaction transaction = Transaction.builder()
                        .date(firstLog.getDate())
                        .amount(firstLog.getAmount().doubleValue())
                        .currency(firstLog.getCurrency())
                        .type(firstLog.getType())
                        .logs(potentialMatches)
                        .tags(new ArrayList<>())
                        .build();
                mergedTransactions.add(transaction);
            }
        }
        log.info("合并交易记录结束，共合并 {} 条", mergedTransactions.size());
        return mergedTransactions;
    }

    
}
