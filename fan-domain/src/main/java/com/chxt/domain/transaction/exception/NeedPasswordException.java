package com.chxt.domain.transaction.exception;

import com.chxt.domain.transaction.constants.TransactionEnums;

public class NeedPasswordException  extends RuntimeException{


	public NeedPasswordException(TransactionEnums.CHANNEL channel) {
		super(channel.getName() + ", 需要密码");
	}
}
