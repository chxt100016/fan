package com.chxt.service;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.transaction.model.vo.MailParseParamVO;
import com.chxt.transaction.InitService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;


@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class TransactionServiceTest {

    @Resource
    private InitService initService;

    @Test
	@Rollback(false)
    public void testTransactionService(){

    }
}
