package com.chxt.transaction;


import com.chxt.domain.transaction.model.vo.MailParseParamVO;
import com.chxt.domain.transaction.service.TransactionLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class InitService {

    @Resource
    private TransactionLogService transactionLogService;

    public void init(MailParseParamVO param) {

        transactionLogService.init(param);
		


    }

}
