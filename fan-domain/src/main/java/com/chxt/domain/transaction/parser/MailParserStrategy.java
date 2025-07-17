package com.chxt.domain.transaction.parser;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

import com.chxt.domain.utils.Mail;



/**
 * 邮件解析策略接口
 */
public interface MailParserStrategy<T> {
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
    List<T> parse(Mail mail, PasswordHelper helper);

    /**
     * 获取策略支持的渠道
     * @return 渠道
     */
    String getChannel();

    Date getTransactionStartDate(Mail mail, List<T> data);

    Date getTransactionEndDate(Mail mail, List<T> data);

    Date getDate(T data);

    BigDecimal getAmount(T data);

    String getCurrency(T data);

    String getType(T data);

    String getMethod(T data);

    String getDescription(T data);

    default String getLogId(T data) {
        return this.getChannel() + ":" + DigestUtils.md5Hex(data.toString());
    }

} 