package com.chxt.domain.utils;




import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.jsoup.Jsoup;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


@Data
@Slf4j
public class Mail {

    private Message message;

    private String subject;

    private String from;

    private String body;

    private Date date;

    private byte[] attachment;

    // 获取并解码文件名
    private String attachmentFileName;

    
    @SneakyThrows
    public Mail(Message message) {
        this.message = message;
        
        if (message.getFrom() != null && message.getFrom().length > 0) {
            this.from = MimeUtility.decodeText(message.getFrom()[0].toString());
        }
        if (message.getSubject() != null) {
            this.subject = message.getSubject();
        }
        if (message.getSentDate() != null) {
            this.date = message.getSentDate();
        }

        this.parseContent();
        
        

    }

    @SneakyThrows
    private void parseContent() {
        if (message.getContent() instanceof Multipart) {
            this.parseMultipart((Multipart) message.getContent());
        } else {
            this.parseBody(message.getContent());
        }
    }

    @SneakyThrows
    private void parseMultipart(Multipart multipart) {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                this.parseAttachment(bodyPart);
            }
            if (bodyPart.getContentType().toLowerCase().contains("text")) {
                this.parseBody(bodyPart.getContent());
            }
       
        }
    }

    @SneakyThrows
    private void parseAttachment(BodyPart bodyPart) {
         // 获取并解码文件名
         String fileName = bodyPart.getFileName();
         if (fileName != null) {
             this.attachmentFileName = MimeUtility.decodeText(fileName);
         }
         // 获取附件数据
         try (InputStream inputStream = bodyPart.getInputStream()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            this.attachment = outputStream.toByteArray();
         } catch (Exception e) {
            log.error("获取邮件附件数据失败", e);
         }


    }

    @SneakyThrows
    private void parseBody(Object content) {
        this.body = content.toString();
    }

    public String getBodyText() {
        return Jsoup.parse(this.body).text();
    }

    public void printInfo() {
        System.out.println("subject: " + this.subject);
        System.out.println("from: " + this.from);
        System.out.println("date: " + DateFormatUtils.format(this.date, "yyyy-MM-dd HH:mm:ss"));
        if (this.attachmentFileName != null) {
            System.out.println("attachmentFileName: " + this.attachmentFileName);
        }
        System.out.println("--------------------------------");
    }


}
