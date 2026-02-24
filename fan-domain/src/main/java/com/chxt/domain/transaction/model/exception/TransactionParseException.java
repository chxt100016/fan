package com.chxt.domain.transaction.model.exception;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.utils.Mail;

import lombok.Getter;

public class TransactionParseException extends RuntimeException {
	
	// 主题(日期): 子类携带消息
	private final static String FORMAT = "%s(%s): %s";


	@Getter
	private TransactionEnums.Channel channel;

	@Getter
	private Mail mail;

	public TransactionParseException(String message, TransactionEnums.Channel channel, Mail mail) {
		super(String.format(FORMAT, mail.getSubject(), DateFormatUtils.format(mail.getDate(), "yyyy-MM-dd HH:mm:ss"), message));
		this.channel = channel;
		this.mail = mail;
	}

	public TransactionParseException(String message, TransactionEnums.Channel channel, Mail mail, Throwable cause) {
		super(message, cause);
		this.channel = channel;
		this.mail = mail;
	}
}
