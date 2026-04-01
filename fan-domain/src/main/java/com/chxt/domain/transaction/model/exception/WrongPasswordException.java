package com.chxt.domain.transaction.model.exception;

import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.utils.Mail;

public class WrongPasswordException extends TransactionParseException {


	public WrongPasswordException(TransactionEnums.Channel channel, Mail mail) {
		super(channel.getName() + "密码错误", channel, mail);
	}
}
