package com.chxt.domain.transaction.parser.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.transaction.model.exception.ParseException;
import com.chxt.domain.transaction.model.exception.PasswordRequiredException;
import com.chxt.domain.transaction.model.exception.WrongPasswordException;
import com.chxt.domain.transaction.parser.MailParserStrategy;
import com.chxt.domain.transaction.parser.PasswordHelper;
import com.chxt.domain.utils.Excel;
import com.chxt.domain.utils.Mail;
import com.chxt.domain.utils.Zip;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AliPayParser implements MailParserStrategy<Map<String,String>> {

    private final static String ALI_PAY_FROM = "service@mail.alipay.com";

    private final static String ALI_PAY_SUBJECT = "支付宝交易流水明细";
    
    private final static String ALIPAY_HEADER_MARKER = "------------------------支付宝（中国）网络技术有限公司  电子客户回单------------------------";
	


    @Override
    public String getFrom() {
        return ALI_PAY_FROM;
    }

    @Override
    public String getSubject() {
       return ALI_PAY_SUBJECT;
    }

    @Override
    public String getChannel() {
        return TransactionEnums.CHANNEL.ALI_PAY.getCode();
    }

    @Override
    public String getType(Map<String,String> data) {
        return StringUtils.endsWith(data.get("收/支"), "支出") ? TransactionEnums.TYPE.EXPENSE.getCode() : TransactionEnums.TYPE.INCOME.getCode();
    }
    
    @Override
    public BigDecimal getAmount(Map<String,String> data) {
        return new BigDecimal(data.get("金额"));
    }
    
    @Override
    public String getCurrency(Map<String,String> data) {
        return TransactionEnums.CURRENCY.CNY.getCode();
    }
    
    @Override
    public String getMethod(Map<String,String> data) {
        return data.get("收/付款方式");
    }
    
    @Override
    public String getDescription(Map<String,String> data) {
        String format = "收/支:%s;交易分类:%s;交易对方:%s;对方账号:%s;商品说明:%s;交易状态:%s;备注:%s;";
        return String.format(format, data.get("收/支"), data.get("交易分类"), data.get("交易对方"), data.get("对方账号"), data.get("商品说明"), data.get("交易状态"), data.get("备注"));
    }
    
    @Override
    @SneakyThrows
    public Date getDate(Map<String,String> data) {
        return DateUtils.parseDate(data.get("交易时间"), "yyyy-MM-dd HH:mm:ss");
    }
    
    @Override
    @SneakyThrows
    public Date getTransactionStartDate(Mail mail, List<Map<String,String>> data) {
        String name = mail.getAttachmentFileName();
        // start time of the day from name,  example: 支付宝交易明细(20250330-20250430).zip
        Pattern pattern = Pattern.compile("支付宝交易明细\\((\\d{8})-(\\d{8})\\).zip");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            String startDateStr = matcher.group(1);
            return DateUtils.parseDate(startDateStr, "yyyyMMdd");
        }
        throw new Exception("支付宝交易明细文件名格式不正确");
    }
    
    @Override
    @SneakyThrows
    public Date getTransactionEndDate(Mail mail, List<Map<String,String>> data) {
        String name = mail.getAttachmentFileName();
        // end time of the day from name,  example: 支付宝交易明细(20250330-20250430).zip
        Pattern pattern = Pattern.compile("支付宝交易明细\\((\\d{8})-(\\d{8})\\).zip");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            String endDateStr = matcher.group(2);
            return DateUtils.parseDate(endDateStr, "yyyyMMdd");
        }
        throw new Exception("支付宝交易明细文件名格式不正确");
    }

    @Override
    @SneakyThrows
    public List<Map<String,String>> parse(Mail mail, PasswordHelper helper) {

        String password = helper.getPassword(TransactionEnums.CHANNEL.ALI_PAY.getCode(), mail.getDate().getTime(), mail.getAttachmentFileName());
		if (password == null) {
			throw new PasswordRequiredException(TransactionEnums.CHANNEL.ALI_PAY, mail);
		}
		
        // 解压ZIP文件
        Zip zip = new Zip(mail.getAttachment(), password);
		if (zip.isWrongPassword()) {
			throw new WrongPasswordException(TransactionEnums.CHANNEL.ALI_PAY, mail);
		}
		
        Excel excel = new Excel(zip.getOne(), ALIPAY_HEADER_MARKER);
        return excel.getDataMap();
    } 

}
