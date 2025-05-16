package com.chxt.domain.transaction.parser.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.chxt.domain.transaction.constants.TransactionEnums;
import com.chxt.domain.transaction.parser.MailParserStrategy;
import com.chxt.domain.utils.Excel;
import com.chxt.domain.utils.HttpOperator;
import com.chxt.domain.utils.Mail;
import com.chxt.domain.utils.Zip;

import lombok.SneakyThrows;

public class WechatPayParser implements MailParserStrategy<Map<String, String>>{

    private static final String FROM = "wechatpay@tencent.com";

    private static final String SUBJECT = "微信支付-账单流水文件";

    private static final String EXCEL_MARKER = "----------------------微信支付账单明细列表--------------------";

    @Override
    public String getFrom() {
        return FROM;
    }

    @Override
    public String getSubject() {
        return SUBJECT;
    }

    @Override
    public List<Map<String, String>> parse(Mail mail) {
        Document doc = Jsoup.parse(mail.getBody());
        Elements aElements = doc.getElementsByTag("a");
        String url = aElements.get(0).attr("href");
        byte[] byteArray = new HttpOperator().uri(url).doGet().byteArray();
        Zip zip = new Zip(byteArray, "192869");
        byte[] bytes = zip.getOne("csv");
        Excel excel = new Excel(bytes, EXCEL_MARKER);
        
        mail.setAttachmentFileName(zip.getOneName("csv"));
        return excel.getDataMap();
        
        
    }

    @Override
    public String getChannel() {
        return TransactionEnums.CHANNEL.WECHAT_PAY.getCode();
    }


    @Override
    @SneakyThrows
    public Date getTransactionStartDate(Mail mail, List<Map<String, String>> data) {
        String name = mail.getAttachmentFileName();
        // start time of the day from name,  example: 微信支付账单(20250313-20250513)——【解压密码可在微信支付公众号查看】.csv
        Pattern pattern = Pattern.compile("微信支付账单\\((\\d{8})-(\\d{8})\\)——【解压密码可在微信支付公众号查看】.csv");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            String startDateStr = matcher.group(1);
            return DateUtils.parseDate(startDateStr, "yyyyMMdd");
        }
        throw new Exception("微信支付账单文件名格式不正确");
    }

    // 微信支付账单(20250313-20250513)——【解压密码可在微信支付公众号查看】.csv
    @Override
    @SneakyThrows
    public Date getTransactionEndDate(Mail mail, List<Map<String, String>> data) {
        String name = mail.getAttachmentFileName();
        // end time of the day from name,  example: 支付宝交易明细(20250330-20250430).zip
        Pattern pattern = Pattern.compile("微信支付账单\\((\\d{8})-(\\d{8})\\)——【解压密码可在微信支付公众号查看】.csv");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            String endDateStr = matcher.group(2);
            return DateUtils.parseDate(endDateStr, "yyyyMMdd");
        }
        throw new Exception("微信支付账单文件名格式不正确");
    }

    @Override
    public Date getDateTime(Map<String, String> data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDateTime'");
    }

    @Override
    public BigDecimal getAmount(Map<String, String> data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAmount'");
    }

    @Override
    public String getCurrency(Map<String, String> data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCurrency'");
    }

    @Override
    public String getType(Map<String, String> data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    }

    @Override
    public String getMethod(Map<String, String> data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMethod'");
    }

    @Override
    public String getDesc(Map<String, String> data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDesc'");
    }


}
