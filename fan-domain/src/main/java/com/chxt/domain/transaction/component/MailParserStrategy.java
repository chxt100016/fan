package com.chxt.domain.transaction.component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson2.JSON;
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
        return this.getChannel() + ":" + this.getDate(data).getTime() + ":" + DigestUtils.md5Hex(JSON.toJSONString(data));
    }

} 