package com.chxt.domain.transaction.model.exception;

import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.utils.Mail;

public class PasswordRequiredException extends TransactionParseException {


	public PasswordRequiredException(TransactionEnums.CHANNEL channel, Mail mail) {
		super(channel.getName() + "需要密码", channel, mail);
	}
}
