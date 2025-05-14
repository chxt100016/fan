package com.chxt.domain.transaction.parser;

import java.util.List;

import com.chxt.domain.transaction.entity.TransactionLog;
import com.chxt.domain.utils.Mail;



/**
 * 邮件解析策略接口
 */
public interface MailParserStrategy {
    /**
     * 获取策略支持的发件人
     * @return 发件人邮箱
     */
    String getFrom();
    
    /**
     * 获取策略支持的邮件主题
     * @return 邮件主题
     */
    String getSubject();
    
    /**
     * 处理邮件并解析数据
     * @param messages 符合条件的邮件列表
     * @return 解析后的交易记录列表
     */
    List<TransactionLog> process(Mail mail);
} 