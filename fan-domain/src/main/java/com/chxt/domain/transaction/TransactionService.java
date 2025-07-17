package com.chxt.domain.transaction;

import java.util.List;

import com.chxt.domain.transaction.model.entity.TransactionChannel;
import com.chxt.domain.transaction.model.vo.MailParseParamVO;
import com.chxt.domain.transaction.parser.MailManager;
import com.chxt.domain.transaction.parser.PasswordHelper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransactionService {
    
    @SneakyThrows
    public static List<TransactionChannel> init(MailParseParamVO param, PasswordHelper passwordHelper) {
        log.info("开始解析交易记录");


        // 注册策略
        MailManager manager = MailManager.Builder()
            .setHost(param.getHost())
            .setUsername(param.getUsername())
            .setPassword(param.getPassword())
			.setPasswordHelper(passwordHelper)
			.addStrategy(param.getParserCode())
            .build();

        // 使用策略管理器处理邮件
        List<TransactionChannel> list = manager.parse(param.getStartDateStr());

        // 输出所有解析结果
        return list;
    }

 

    
}
