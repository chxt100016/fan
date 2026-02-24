package com.chxt.web.transaction;

import com.chxt.domain.transaction.model.entity.Transaction;
import com.chxt.domain.transaction.model.vo.AnalysisParamVO;
import org.springframework.web.bind.annotation.*;

import com.chxt.domain.transaction.model.vo.MailParseParamVO;
import com.chxt.transaction.InitService;

import jakarta.annotation.Resource;

import java.util.List;

@RestController
@RequestMapping("/transaction/init")
public class InitController {

    @Resource
    private InitService initService;

    @PostMapping("/load")
    public void init(@RequestBody MailParseParamVO param) {
        this.initService.init(param);
    }

    @PostMapping("/analysis")
    public List<Transaction> analysis(@RequestBody AnalysisParamVO param) {
        return this.initService.analysis(param);
    }

}
