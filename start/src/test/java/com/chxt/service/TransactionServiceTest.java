package com.chxt.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.chxt.transaction.InitService;

import jakarta.annotation.Resource;


@SpringBootTest
@RunWith(SpringRunner.class)
public class TransactionServiceTest {

    @Resource
    private InitService initService;

    @Test
    public void testTransactionService(){
        this.initService.init();
    }
}
