package com.chxt.domain.transaction.parser.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;

import com.chxt.domain.transaction.entity.TransactionLog;
import com.chxt.domain.transaction.parser.MailParserStrategy;
import com.chxt.domain.utils.Excel;
import com.chxt.domain.utils.Mail;
import com.chxt.domain.utils.Zip;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AliPayStrategy implements MailParserStrategy {

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
    @SneakyThrows
    public List<TransactionLog> process(Mail mail) {
        try {
            
            
            // 要求用户输入解压密码
            // Scanner scanner = new Scanner(System.in);
            // System.out.print("请输入支付宝交易流水明细ZIP文件的解压密码: ");
            // String password = scanner.nextLine();
            String password = "142268";
            // 解压ZIP文件
            Zip zip = new Zip(mail.getAttachment(), password);

            Excel excel = new Excel(zip.getOne(), ALIPAY_HEADER_MARKER);
            List<Map<String,String>> rows = excel.getDataMap();
            
            List<TransactionLog> transactionLogs = new ArrayList<>(); 
            for (Map<String,String> row : rows) {
                
                TransactionLog item = TransactionLog.builder()
                    .dateTime(DateUtils.parseDate(row.get("交易时间"), "yyyy-MM-dd HH:mm:ss"))
                    .amount(Double.parseDouble(row.get("金额")))
                    .method(row.get("收/付款方式"))
                    .currency("CNY")
                    .type(row.get("收/支"))
                    .desc(String.format("交易对方: %s;对方账号: %s;商品说明: %s", row.get("交易对方"), row.get("对方账号"), row.get("商品说明")))
                    .build();
                transactionLogs.add(item);
            }
            return transactionLogs;
        } catch (Exception e) {
            log.error("处理支付宝交易流水明细邮件失败", e);
            return new ArrayList<>();
        }
    } 

}
