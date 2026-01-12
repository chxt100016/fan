package com.chxt.domain.transaction.model.vo;

import com.chxt.domain.utils.Mail;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ChannelMail {

    private String userId;

    private String channel;

    private List<Mail> mails;


}
