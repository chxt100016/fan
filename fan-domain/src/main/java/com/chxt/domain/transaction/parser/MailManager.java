package com.chxt.domain.transaction.parser;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.chxt.domain.transaction.entity.TransactionLog;
import com.chxt.domain.utils.Mail;
import com.chxt.domain.utils.MailClient;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 邮件解析策略管理器
 */
@Slf4j
public class MailManager {
    
    private final List<MailParserStrategy> strategies = new ArrayList<>();
    
    /**
     * 添加邮件解析策略
     * @param strategy 解析策略
     * @return 当前管理器实例
     */
    public MailManager addStrategy(MailParserStrategy strategy) {
        strategies.add(strategy);
        return this;
    }
    
    
    
    /**
     * 使用Mail对象处理所有策略的邮件

     * @param startDateStr 开始`日期
     * @return 所有交易记录列表
     */
    @SneakyThrows
    public List<TransactionLog> process(MailClient mailClient, String startDateStr) {
        List<TransactionLog> allLogs = new ArrayList<>();
        
        // 一次性获取所有策略的邮件，避免多次打开关闭连接
        List<List<Mail>> allStrategyMails = new ArrayList<>();
        
        // 处理每个策略
        for (MailParserStrategy strategy : strategies) {
            // 根据策略查询对应的邮件
            List<Mail> messages = mailClient.search(startDateStr, strategy.getFrom(), strategy.getSubject());
            allStrategyMails.add(messages);
            
            log.info("找到符合条件的邮件数量:{}, from:{}, subject:{}", messages.size(), strategy.getFrom(), strategy.getSubject());

        }
        
        // 处理所有获取到的邮件
        for (int i = 0; i < strategies.size(); i++) {
            MailParserStrategy strategy = strategies.get(i);
            List<Mail> mails = allStrategyMails.get(i);
            
            // 如果有符合条件的邮件，使用相应策略处理
            if (mails.size() == 0) {
               continue;
            }

            for (Mail mail : mails) {
                List<TransactionLog> logs = strategy.process(mail);
                if (CollectionUtils.isEmpty(logs)) {
                    continue;
                }
                allLogs.addAll(logs);
            }
        }
        
        return allLogs;
    }
    
  
} 