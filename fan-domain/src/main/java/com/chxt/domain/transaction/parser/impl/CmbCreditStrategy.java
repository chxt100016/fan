package com.chxt.domain.transaction.parser.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.chxt.domain.transaction.entity.TransactionLog;
import com.chxt.domain.transaction.parser.MailParserStrategy;
import com.chxt.domain.utils.Mail;

import lombok.SneakyThrows;

/**
 * 招商银行信用管家邮件解析策略
 */
public class CmbCreditStrategy implements MailParserStrategy {
    
    private static final String FROM = "ccsvc@message.cmbchina.com";
    private static final String SUBJECT = "每日信用管家";
    
    @Override
    public String getFrom() {
        return FROM;
    }
    
    @Override
    public String getSubject() {
        return SUBJECT;
    }
    
    @Override
    @SneakyThrows
    public List<TransactionLog> process(Mail mail) {
        String content = mail.getBody();
        // 解析邮件内容，提取消费记录
        return parseEmailContent(content);
    }
    
    /**
     * 解析邮件内容，提取交易记录
     */
    @SneakyThrows
    private List<TransactionLog> parseEmailContent(String htmlContent) {
        // 解析HTML
        Document doc = Jsoup.parse(htmlContent);
        
        Element detail = doc.getElementById("fixBand3");
        if (detail == null) {
            return new ArrayList<>();
        }
        
        Element title = detail.getElementById("loopHeader1");
        if (title == null) {
            return new ArrayList<>();
        }
        
        String dateStr = title.text().split(" ")[0];
        List<TransactionLog> res = new ArrayList<>();

        List<Element> items = detail.getElementsByAttributeValueMatching("id", "fixBand4");
        for(Element item : items) {
            String[] split = item.text().split(" ");
            
            String dateTimeStr = dateStr + " " + split[0];
            Date dateTime = DateUtils.parseDate(dateTimeStr, "yyyy/MM/dd HH:mm:ss");
            res.add(TransactionLog.builder()
                .amount(Double.parseDouble(split[2]))
                .currency(split[1])
                .dateTime(dateTime)
                .type(split[4])
                .desc(split[5])
                .build()
            );
        }
        
        return res;
    }
  
    
   
} 