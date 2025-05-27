package com.chxt.domain.transaction;

import java.util.List;

import com.chxt.domain.transaction.constants.TransactionEnums;
import com.chxt.domain.transaction.entity.TransactionChannel;
import com.chxt.domain.transaction.parser.MailManager;
import com.chxt.domain.transaction.parser.impl.CgbcCreditParser;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransactionService {
    
    @SneakyThrows
    public static List<TransactionChannel>  init() {
        log.info("开始解析交易记录");
        // 配置信息
        String host = "imap.qq.com";  // QQ邮箱的IMAP服务器
        String username = "546555918@qq.com";  // 你的QQ邮箱地址
        String password = "nnfjkmehqypgbbhc";  // 你的QQ邮箱授权码（不是登录密码）
        String startDateStr = "2025-05-01";  // 开始日期，格式为yyyy-MM-dd
        

        // 注册策略
        MailManager manager = MailManager.Builder()
            .setHost(host)
            .setUsername(username)
            .setPassword(password)
            // .addStrategy(new WechatPayParser())
            // .addStrategy(new CgbcCreditParser())
            // .addStrategy(new AliPayParser())
            // .addStrategy(new CmbCreditParser())
            .build();
        // strategyManager.addStrategy(new CmbCreditStrategy());
        // manager.addStrategy(new AliPayParser());
        // manager.addStrategy(new WechatPayParser());

        // 使用策略管理器处理邮件
        List<TransactionChannel> list = manager.parse(startDateStr, true);





            


        // 输出所有解析结果
        return list;
    }

    public static void main(String[] args) {
        List<TransactionChannel> list = init();
        Integer income = list.stream().flatMap(it -> it.getLogs().stream()).filter(it -> it.getType() == TransactionEnums.TYPE.INCOME.getCode()).mapToInt(it -> it.getAmount().intValue()).sum();
        Integer expense = list.stream().flatMap(it -> it.getLogs().stream()).filter(it -> it.getType() == TransactionEnums.TYPE.EXPENSE.getCode()).mapToInt(it -> it.getAmount().intValue()).sum();

        list.forEach(TransactionChannel::printLogs);

        log.info("收入：{}", income);
        log.info("支出：{}", expense);
    }


    
}
