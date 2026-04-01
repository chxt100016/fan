package com.chxt.domain.transaction.model.exception;

import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.utils.Mail;

public class ParseException extends TransactionParseException {

	public ParseException(TransactionEnums.Channel channel, Mail mail) {
		super(channel.getName() + "邮件解析失败", channel, mail);
	}

}
