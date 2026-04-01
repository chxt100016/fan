package com.chxt.domain.transaction.model.exception;

import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.utils.Mail;

public class FileOutOfTimeException extends TransactionParseException{

	public FileOutOfTimeException(TransactionEnums.Channel channel, Mail mail) {
		super(channel.getName() + "文件已过期, 请重新导出", channel, mail);
	}
}
