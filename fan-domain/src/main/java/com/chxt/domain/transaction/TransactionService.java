package com.chxt.domain.transaction;

import java.util.List;

import com.chxt.domain.transaction.entity.TransactionChannel;
import com.chxt.domain.transaction.parser.MailManager;
import com.chxt.domain.transaction.parser.impl.WechatPayParser;
import com.chxt.domain.utils.MailClient;

import lombok.SneakyThrows;

public class TransactionService {
    
    @SneakyThrows
    public static List<TransactionChannel>  init() {
        // 配置信息
        String host = "imap.qq.com";  // QQ邮箱的IMAP服务器
        String username = "546555918@qq.com";  // 你的QQ邮箱地址
        String password = "nnfjkmehqypgbbhc";  // 你的QQ邮箱授权码（不是登录密码）
        String startDateStr = "2025-05-01";  // 开始日期，格式为yyyy-MM-dd
        

        try (MailClient mailClient = new MailClient(host, username, password, true)) {
         
         
            
            // 注册策略
            MailManager manager = new MailManager();
            // strategyManager.addStrategy(new CmbCreditStrategy());
            // manager.addStrategy(new AliPayParser());
            manager.addStrategy(new WechatPayParser());
        
            // 使用策略管理器处理邮件
            List<TransactionChannel> list = manager.parse(mailClient, startDateStr);

        
            // 输出所有解析结果
            return list;
           
            
        } catch (Exception e) {
            System.err.println("处理邮件时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }


    
}
