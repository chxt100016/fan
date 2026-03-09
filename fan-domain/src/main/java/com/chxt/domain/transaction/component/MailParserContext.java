package com.chxt.domain.transaction.component;


import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.transaction.model.exception.PasswordRequiredException;
import com.chxt.domain.utils.Mail;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class MailParserContext<T> {

    private TransactionEnums.Channel channel;

    private String startDate;

    private String endDate;

    private Mail mail;

    private List<T> data;

    private Map<String, String> ext;

    private PasswordHelper passwordHelper;

    public MailParserContext(String channel, Mail mail, PasswordHelper passwordHelper) {
        this.mail = mail;
        this.channel = TransactionEnums.Channel.valueOf(channel);
    }


    public void putExt(String key, Object value) {
        if (ext == null) {
            ext = new HashMap<>();
        }
        ext.put(key, value.toString());
    }

    public String getExt(String key) {
        if (ext == null) {
            return null;
        }
        return ext.get(key);
    }

    public String getPassword() {
        String password = this.passwordHelper.getPassword(this.channel.getCode(), mail.getDate().getTime(), mail.getAttachmentFileName());
        if (password == null) {
            throw new PasswordRequiredException(TransactionEnums.Channel.ALI_PAY, mail);
        }
        return password;
    }

    public byte[] getAttachment() {
        return this.mail.getAttachment();
    }

    public String getAttachmentFileName() {
        return this.mail.getAttachmentFileName();
    }

    public String getContent() {
        return this.mail.getBody();
    }

    public String getSubject() {
        return this.mail.getSubject();
    }

}
