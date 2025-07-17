package com.chxt.domain.transaction.parser.impl;

import java.math.BigDecimal;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.transaction.parser.MailParserStrategy;
import com.chxt.domain.transaction.parser.PasswordHelper;
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
        return TransactionEnums.CHANNEL.CMB_CREDIT.getCode();
    }

    @Override
    public String getType(String[] data) {
        return new BigDecimal(data[2]).compareTo(BigDecimal.ZERO) > 0 ? TransactionEnums.TYPE.EXPENSE.getCode() : TransactionEnums.TYPE.INCOME.getCode();
    }
    
    @Override
    public BigDecimal getAmount(String[] data) {
        return new BigDecimal(data[2]).abs();
    }
    
    @Override
    public String getCurrency(String[] data) {
        return TransactionEnums.CURRENCY.CNY.getCode();
    }
    
    @Override
    public String getMethod(String[] data) {
        return null;
    }
    
    @Override
    public String getDescription(String[] data) {
        return String.format("类型:%s;备注:%s;", data[4], data[5]);
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
        Element title = detail.getElementById("loopHeader1");
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