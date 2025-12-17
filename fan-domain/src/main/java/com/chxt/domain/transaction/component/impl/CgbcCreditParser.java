package com.chxt.domain.transaction.component.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.transaction.component.MailParserStrategy;
import com.chxt.domain.transaction.component.PasswordHelper;
import com.chxt.domain.utils.Mail;

import lombok.SneakyThrows;

public class CgbcCreditParser implements MailParserStrategy<List<String>> {

    private static final String FROM = "creditcard@cgbchina.com.cn";
    
    private static final String SUBJECT = "电子账单";

    // 账单交易明细图片地址
    private static final String DETAIL_PIC_URL = "https://static.95508.com/mmg/cbs/ad/202311201712359142719870710702_%E6%99%AE%E9%80%9A%E7%89%88%E5%BA%95%E5%9B%BE%E6%A0%87%E9%A2%98320231025.jpg";


    @Override
    public String getFrom() {
        return FROM;
    }

    @Override
    public String getSubject() {
        return SUBJECT;
    }

    /**
     * 交易日期	     入账日期     交易摘要                  交易金额    交易货币	入账金额	入账货币
     * 20xx/xx/xx   20xx/xx/xx  (还款)银联跨行自动转账还款  -1,000.00  人民币      -1,000.00  人民币
     */
    @Override
    public List<List<String>> parse(Mail mail, PasswordHelper helper) {
        String content = mail.getBody();

        Document doc = Jsoup.parse(content);

        Elements rows = doc.getElementsByTag("body").get(0).child(0).child(0).children();
        boolean isStart = false;

        List<List<String>> data = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            Element row = rows.get(i);
            if (!isStart) {
                Elements a = row.getElementsByTag("table");
                if (a.size() > 0) {
                    String background = a.get(0).attr("background");
                    if (StringUtils.equals(background, DETAIL_PIC_URL)) {
                        isStart = true;
                        i += 2;
                    }
                }
                continue;
            }
            if (!isStart) {
                continue;
            }
            if (StringUtils.isBlank(row.text())) {
                continue;
            }
            if (row.text().startsWith("积分类型")) {
                break;
            }


            List<String> rowData = Arrays.asList(row.text().split("\\s+"));

            data.add(rowData);
            
        }

        return data;
    }

    @Override
    public String getChannel() {
        return TransactionEnums.CHANNEL.CGBC_CREDIT.getCode();
    }


    // 账单周期:2025/04/24-2025/05/23
    @Override
    @SneakyThrows
    public Date getTransactionStartDate(Mail mail, List<List<String>> data) {
        String content = mail.getBody();
        Document doc = Jsoup.parse(content);

        Elements rows = doc.getElementsByTag("body").get(0).child(0).child(0).children();

        for (int i = 0; i < rows.size(); i++) {
            Element row = rows.get(i);
            if (row.text().startsWith("账单周期")) {
                String str = row.text();
                Pattern pattern = Pattern.compile("账单周期:(\\d{4}/\\d{2}/\\d{2})-(\\d{4}/\\d{2}/\\d{2})");
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {
                    String date = matcher.group(1);
                    return DateUtils.parseDate(date, "yyyy/MM/dd");
                }
            }
        }
        return null;
    }
    

    // 账单周期:2025/04/24-2025/05/23
    @Override
    @SneakyThrows
    public Date getTransactionEndDate(Mail mail, List<List<String>> data) {
        String content = mail.getBody();
        Document doc = Jsoup.parse(content);

        Elements rows = doc.getElementsByTag("body").get(0).child(0).child(0).children();

        for (int i = 0; i < rows.size(); i++) {
            Element row = rows.get(i);
            if (row.text().startsWith("账单周期")) {
                String str = row.text();
                Pattern pattern = Pattern.compile("账单周期:(\\d{4}/\\d{2}/\\d{2})-(\\d{4}/\\d{2}/\\d{2})");
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {
                    String date = matcher.group(2);
                    return DateUtils.parseDate(date, "yyyy/MM/dd");
                }
            }
        }
        return null;
    }

    @Override
    @SneakyThrows
    public Date getDate(List<String> data) {
        String date = data.get(0);
        return DateUtils.parseDate(date, "yyyy/MM/dd");
    }

    @Override
    public BigDecimal getAmount(List<String> data) {
        String amount = data.get(3).replaceAll(",", "");
        return new BigDecimal(amount).abs();
    }

    @Override
    public String getCurrency(List<String> data) {
        String currency = data.get(4);
        switch (currency) {
            case "人民币":
                return TransactionEnums.CURRENCY.CNY.getCode();
            default:
                return null;
        }
    }

    @Override
    public String getType(List<String> data) {
        return data.get(3).startsWith("-") ? TransactionEnums.TYPE.INCOME.getCode() : TransactionEnums.TYPE.EXPENSE.getCode();
    }

    @Override
    public String getMethod(List<String> data) {
        return null;
    }

    @Override
    public String getDescription(List<String> data) {
        return data.get(2);
    }
}
