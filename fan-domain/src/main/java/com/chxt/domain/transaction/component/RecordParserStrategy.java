package com.chxt.domain.transaction.component;

import com.alibaba.fastjson2.JSON;
import com.chxt.domain.transaction.model.vo.LogDescVO;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigDecimal;
import java.util.Date;



/**
 * 邮件解析策略接口
 */
public interface RecordParserStrategy<T> {
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
     */
     void parse(RecordParserContext<T> context);

    /**
     * 获取策略支持的渠道
     * @return 渠道
     */
    String getChannel();

    Date getTransactionStartDate(RecordParserContext<T> context);

    Date getTransactionEndDate(RecordParserContext<T> context);

    Date getDate(T data);

    BigDecimal getAmount(T data);

    String getCurrency(T data);

    String getType(T data);

    String getMethod(T data);

    LogDescVO getDescription(T data);

    String getCounterparty(T data);

    default String getLogId(T data) {
        return this.getChannel() + ":" + this.getDate(data).getTime() + ":" + DigestUtils.md5Hex(JSON.toJSONString(data));
    }

} 