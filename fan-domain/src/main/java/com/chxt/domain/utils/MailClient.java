package com.chxt.domain.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

import javax.mail.Session;
import javax.mail.Store;

import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 邮件查询类
 */
@Slf4j
public class MailClient implements AutoCloseable{

    
    // 添加store和inbox字段
    private final Store store;
    private final Folder defaultInbox;
    private boolean printInfo;

    /**
     * 构造方法
     * @param host 邮件服务器
     * @param username 用户名
     * @param password 密码或授权码
     */
    @SneakyThrows
    public MailClient(String host, String username, String password, boolean printInfo) {
        // 设置邮箱的属性
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");  // 使用IMAPS协议
        props.setProperty("mail.imaps.host", host);
        props.setProperty("mail.imaps.port", "993");
        
        // 获取Session对象
        Session session = Session.getInstance(props, null);
        // 连接到邮件服务器
        this.store = session.getStore("imaps");
        this.store.connect(host, username, password);
        
        // 打开收件箱
        this.defaultInbox = store.getFolder("INBOX");
        this.defaultInbox.open(Folder.READ_ONLY);

        this.printInfo = printInfo;
        
    }

    
  
    
    /**
     * 搜索邮件
     * @param startDateStr 开始日期，格式为yyyy-MM-dd
     * @param from 发件人
     * @param subject 邮件主题
     * @return 符合条件的邮件数组
     */
    @SneakyThrows
    public List<Mail> search(String startDateStr, String endDateStr, String from, String subject) {
        // 只用date和from条件向服务器请求
        SearchTerm searchTerm = this.getServerSearchTerm(startDateStr, endDateStr, from);
        Message[] messages = defaultInbox.search(searchTerm);
        List<Mail> res = postFilter(messages, subject);
        if (this.printInfo) {
            res.forEach(Mail::printInfo);
        }
        return res;
    }

    @SneakyThrows
    private List<Mail> postFilter(Message[] messages, String subject) {
        if (StringUtils.isBlank(subject)) {
            return Arrays.stream(messages).map(Mail::new).toList();
        }

        List<Mail> res = new ArrayList<>();
        for (Message message : messages) {
            String messageSubject = message.getSubject();
            if (messageSubject != null && messageSubject.contains(subject)) {
                res.add(new Mail(message));
            }
        }
        return res;
    }

    /**
     * 获取服务器搜索条件（只包含date和from）
     */
    @SneakyThrows
    private SearchTerm getServerSearchTerm(String startDateStr, String endDateStr, String from) {
        List<SearchTerm> list = new ArrayList<>();
        
        // 开始时间
        if (StringUtils.isNotBlank(startDateStr)) {
            Date startDate = DateUtils.parseDate(startDateStr, "yyyy-MM-dd");
            SearchTerm dateTerm = new ReceivedDateTerm(ComparisonTerm.GE, startDate);
            list.add(dateTerm);
        }

        // 结束时间
        if(StringUtils.isNotBlank(endDateStr)) {
            Date endDate = DateUtils.parseDate(endDateStr, "yyyy-MM-dd");
            SearchTerm dateTerm = new ReceivedDateTerm(ComparisonTerm.GE, endDate);
            list.add(dateTerm);
        }
        
        // 发件人
        if (StringUtils.isNotBlank(from)) {
            SearchTerm fromTerm = new FromStringTerm(from);
            list.add(fromTerm);
        }

        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            // 日期和发件人两个条件
            return new AndTerm(list.get(0), list.get(1));
        }
    }
    
    @SneakyThrows
    @Override
    public void close() {
        try {
            // 先检查默认收件箱是否已关闭
            if (this.defaultInbox != null && this.defaultInbox.isOpen()) {
                this.defaultInbox.close(false);  // false表示不要删除标记为删除的邮件
            }
            
            // 然后关闭存储
            if (this.store != null && this.store.isConnected()) {
                this.store.close();
            }
        } catch (MessagingException e) {
            System.err.println("关闭邮件连接错误: " + e.getMessage());
        }
    }
}
