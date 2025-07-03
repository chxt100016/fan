package com.chxt.web.transaction;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chxt.domain.transaction.model.vo.MailParseParamVO;
import com.chxt.transaction.InitService;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/transaction")
public class InitController {

    @Resource
    private InitService initService;

    @GetMapping("/init")
    public void init(@RequestBody MailParseParamVO param) {
        initService.init(param);
    }
}
