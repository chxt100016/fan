package com.chxt.domain.transaction;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.chxt.domain.transaction.entity.TransactionChannel;
import com.chxt.domain.transaction.entity.TransactionLog;
import com.chxt.domain.transaction.parser.MailManager;
import com.chxt.domain.transaction.parser.impl.AliPayParser;
import com.chxt.domain.transaction.parser.impl.WechatPayParser;
import com.chxt.domain.utils.Mail;
import com.chxt.domain.utils.MailClient;

import lombok.SneakyThrows;

public class TransactionService {
    
    @SneakyThrows
    public static void main(String[] args) {
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
            System.out.println("总共解析到 " + list.get(0).getLogs().size() + " 条交易记录");
            write(list.get(0).getLogs().stream().toList());
           
            
        } catch (Exception e) {
            System.err.println("处理邮件时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void write(List<TransactionLog> data) {
          // 将交易记录保存到JSON文件
          String jsonFilePath = "./transactionLog.json";
          String jsonContent = JSON.toJSONString(data);
          try {
              java.nio.file.Files.write(
                  java.nio.file.Paths.get(jsonFilePath),
                  jsonContent.getBytes(java.nio.charset.StandardCharsets.UTF_8)
              );
              System.out.println("交易记录已保存到: " + jsonFilePath);
          } catch (Exception e) {
              System.err.println("保存JSON文件时发生错误: " + e.getMessage());
              e.printStackTrace();
          }
    }

    
}
