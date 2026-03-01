package com.chxt.domain.transaction.component.impl;

import com.chxt.domain.transaction.component.MailParserContext;
import com.chxt.domain.transaction.component.MailParserStrategy;
import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.transaction.model.exception.FileOutOfTimeException;
import com.chxt.domain.utils.Excel;
import com.chxt.domain.utils.Http;
import com.chxt.domain.utils.Zip;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void parse(MailParserContext<Map<String, String>> context) {
        Document doc = Jsoup.parse(context.getContent());
        Elements aElements = doc.getElementsByTag("a");
        String url = aElements.get(0).attr("href");
        Http http = Http.uri(url).doGet();
        if (http.result().contains("当前文件已过期")) {
            throw new FileOutOfTimeException(TransactionEnums.Channel.WECHAT_PAY, context.getMail());
        }

        byte[] byteArray = http.byteArray();
        Zip zip = new Zip(byteArray, context.getPassword());
        byte[] bytes = zip.getOne("xlsx");
        Excel excel = new Excel(bytes, EXCEL_MARKER).xlsx();

        context.setData(excel.parseBytes());
    }

    @Override
    public String getChannel() {
        return TransactionEnums.Channel.WECHAT_PAY.getCode();
    }


    @Override
    @SneakyThrows
    public Date getTransactionStartDate(MailParserContext<Map<String, String>> context) {
        String name = context.getSubject();
        // start time of the day from name,  example: 微信支付账单(20250313-20250513)——【解压密码可在微信支付公众号查看】.csv
        Pattern pattern = Pattern.compile("微信支付-账单流水文件\\((\\d{8})-(\\d{8})\\).*");
        Matcher matcher = pattern.mat
        cher(name);
        if (matcher.find()) {
            String startDateStr = matcher.group(1);
            return DateUtils.parseDate(startDateStr, "yyyyMMdd");
        }


        throw new Exception("微信支付账单文件名格式不正确");
    }

    // 微信支付账单(20250313-20250513)——【解压密码可在微信支付公众号查看】.csv
    @Override
    @SneakyThrows
    public Date getTransactionEndDate(MailParserContext<Map<String, String>> context) {
        String name = context.getSubject();
        // end time of the day from name,  example: 微信支付账单(20250313-20250513)——【解压密码可在微信支付公众号查看】.csv
//        微信支付账单流水文件(20251201-20260223)——【解压密码可在微信支付公众号查看】.xlsx
        Pattern pattern = Pattern.compile("微信支付账单流水文件\\((\\d{8})-(\\d{8})\\)——【解压密码可在微信支付公众号查看】.xlsx");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            String endDateStr = matcher.group(2);
            return DateUtils.parseDate(endDateStr, "yyyyMMdd");
        }
        throw new Exception("微信支付账单文件名格式不正确");
    }

    @Override
    @SneakyThrows
    public Date getDate(Map<String, String> data) {
        return DateUtils.parseDate(data.get("交易时间"), "yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public BigDecimal getAmount(Map<String, String> data) {
        return new BigDecimal(data.get("金额(元)").replace("¥", "").trim());
    }

    @Override
    public String getCurrency(Map<String, String> data) {
        return TransactionEnums.Currency.CNY.getCode();
    }

    @Override
    public String getType(Map<String, String> data) {
        return switch (data.get("收/支")) {
            case "收入" -> TransactionEnums.Type.INCOME.getCode();
            case "支出" -> TransactionEnums.Type.EXPENSE.getCode();
            default -> null;
        };
    }

    @Override
    public String getMethod(Map<String, String> data) {
        return data.get("支付方式");
    }

    @Override
    public String getDescription(Map<String, String> data) {
        String format = "收/支:%s;交易类型:%s;交易对方:%s;商品:%s;交易状态:%s;备注:%s;当前状态:%s;";
        return String.format(format, data.get("收/支"), data.get("交易类型"), data.get("交易对方"), data.get("商品"), data.get("交易状态"), data.get("备注"), data.get("当前状态"));
    }

    @Override
    public String getCounterparty(Map<String, String> data) {
        return "";
    }


}
