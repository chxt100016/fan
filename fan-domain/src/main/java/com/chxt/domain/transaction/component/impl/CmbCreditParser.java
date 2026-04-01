package com.chxt.domain.transaction.component.impl;

import com.chxt.domain.transaction.component.RecordParserContext;
import com.chxt.domain.transaction.component.RecordParserStrategy;
import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.transaction.model.vo.LogDescVO;
import com.chxt.domain.utils.DateStandardUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
/**
 * 招商银行信用管家邮件解析策略
 */
public class CmbCreditParser implements RecordParserStrategy<String[]> {
    
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
    @SneakyThrows
    public void parse(RecordParserContext<String[]> context) {

        Document doc = Jsoup.parse(context.getContent());
        Element detail = doc.getElementById("fixBand3");
        if (Objects.isNull(detail)) {
            return;
        }
        Element title = detail.getElementById("loopHeader1");
        if (Objects.isNull(title)) {
            return;
        }
        String dateStr = title.text().split(" ")[0];
        List<Element> items = detail.getElementsByAttributeValueMatching("id", "fixBand4");

        List<String[]> res = new ArrayList<>();
        for(Element item : items) {
            String[] split = item.text().split(" ");
            split[0] = dateStr + " " + split[0];
            res.add(split);
        }

        context.setData(res);
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
    public LogDescVO getDescription(String[] data) {

        LogDescVO desc = new LogDescVO();
        if (data.length >= 5) {
            desc.put("类型", data[4]);
        }
        if (data.length >= 6) {
            String[] info = data[5].split("-");
            desc.put("来源", info[0]);
            desc.put("备注", info[1]);
        }

        return desc;
    }

    @Override
    public String getCounterparty(String[] data) {
        if (data.length >= 6) {
            return data[5];
        }
        return "";

    }

    @Override
    @SneakyThrows
    public Date getDate(String[] data) {
        return DateUtils.parseDate(data[0], "yyyy/MM/dd HH:mm:ss");
    }
    
    @Override
    public Date getTransactionStartDate(RecordParserContext<String[]> context) {

        return DateStandardUtils.addDate(context.getStartDate(), -1);
    }
    
    @Override
    public Date getTransactionEndDate(RecordParserContext<String[]> context) {
        return DateStandardUtils.addDate(context.getEndDate(), -1);
    }
} 