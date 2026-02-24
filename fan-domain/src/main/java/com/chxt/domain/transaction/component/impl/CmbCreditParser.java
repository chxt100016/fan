package com.chxt.domain.transaction.component.impl;

import java.math.BigDecimal;
import java.util.*;

import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.transaction.component.MailParserStrategy;
import com.chxt.domain.transaction.component.PasswordHelper;
import com.chxt.domain.utils.Mail;

import lombok.SneakyThrows;

/**
/**
 * 招商银行信用管家邮件解析策略
 */
public class CmbCreditParser implements MailParserStrategy<String[]> {
    
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
    public String getChannel() {
        return TransactionEnums.Channel.CMB_CREDIT.getCode();
    }

    @Override
    public String getType(String[] data) {
        return new BigDecimal(data[2]).compareTo(BigDecimal.ZERO) > 0 ? TransactionEnums.Type.EXPENSE.getCode() : TransactionEnums.Type.INCOME.getCode();
    }
    
    @Override
    public BigDecimal getAmount(String[] data) {
        return new BigDecimal(data[2]).abs();
    }
    
    @Override
    public String getCurrency(String[] data) {
        return TransactionEnums.Currency.CNY.getCode();
    }
    
    @Override
    public String getMethod(String[] data) {
        return null;
    }
    
    @Override
    public String getDescription(String[] data) {
        StringBuilder sb = new StringBuilder();

        if (data.length >= 5) {
            sb.append("类型:").append(data[4]).append(";");
        }
        if (data.length >= 6) {
            sb.append("备注:").append(data[5]).append(";");
        }
        return sb.toString();
    }
    
    @Override
    @SneakyThrows
    public Date getDate(String[] data) {
        return DateUtils.parseDate(data[0], "yyyy/MM/dd HH:mm:ss");
    }
    
    @Override
    public Date getTransactionStartDate(Mail mail, List<String[]> data) {
        // start time of the day
        Date date = getDate(data.get(0));
        return DateUtils.truncate(date, Calendar.HOUR_OF_DAY);
    }
    
    @Override
    public Date getTransactionEndDate(Mail mail, List<String[]> data) {
        // the same day
        return getTransactionStartDate(mail, data);
    }
    
    @Override
    @SneakyThrows
    public List<String[]> parse(Mail mail, PasswordHelper helper) {
        
        Document doc = Jsoup.parse(mail.getBody());
        Element detail = doc.getElementById("fixBand3");
        if (Objects.isNull(detail)) {
            return List.of();
        }
        Element title = detail.getElementById("loopHeader1");
        if (Objects.isNull(title)) {
            return List.of();
        }
        String dateStr = title.text().split(" ")[0];
        List<Element> items = detail.getElementsByAttributeValueMatching("id", "fixBand4");

        List<String[]> res = new ArrayList<>();
        for(Element item : items) {
            String[] split = item.text().split(" ");
            split[0] = dateStr + " " + split[0];
            res.add(split);
        }
        return res;
    }

} 