package com.chxt.fantasticmonkey.controller.job;

import com.alibaba.fastjson.JSONObject;
import com.chxt.fantastic.common.http.HttpOperator;
import com.chxt.fantasticmonkey.domain.token.TokenEnum;
import com.chxt.fantasticmonkey.domain.token.TokenFactory;
import com.chxt.fantasticmonkey.infrastructure.wechatWork.WechatWorkClient;
import lombok.Data;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


@Configuration
@EnableScheduling
public class DailyElectricityJob {

    @Resource
    private WechatWorkClient wechatWorkClient;


    @Scheduled(cron = "0 0 0 19 * ?")
    public void electricity() {
        Date today = new Date();
        Date yesterday = DateUtils.addDays(today, -1);
        String result = new HttpOperator()
                .uri("https://wx2.zj.sgcc.com.cn/ldx/zljl/queryData/dailyElectricity")
                .header("Host", "wx2.zj.sgcc.com.cn")
                .header("Accept", "application/json, text/plain, */*")
                .header("params", "GlvVfC69WW22IWEtPMQeNPelPJSh4aaN6TPudbnbA59bfTIvGAmtrhRGe1sJEu9YKqhaYm8Fh6kcTUC6WsKXXHNMM2ja4SpIC/mDY8mmps3vzmo4c5GdWJ1VOThBX8ljOu400ep3zvZGRaUzHOSLeZpJTbftBtqWpcmwCoiji3c3qFUoMWoABxJi8HlYNf/C5K+DjB3qDd15v39DtlzLnBE+saZ8qicE03vlOe0jJyyFZUvxdp0Nm963+vaqIDRF9jNKnq4fSuekf9cVsW/zk3dzbG206mQT94Yd/FOz7MYXVKR2ur+0q06Ru9ieqcZOjo+2EbB97ydn3yJeCxBH6g==")
                .header("Accept-Language", "zh-CN,zh-Hans;q=0.9")
                .header("Content-Type", "text/plain")
                .header("Origin", "https://wx2.zj.sgcc.com.cn")
                .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 16_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/8.0.31(0x18001f37) NetType/WIFI Language/zh_CN")
                .header("Referer", "https://wx2.zj.sgcc.com.cn/ldx/zljl/oa/home?consNo=3306120926333&orgNo=33401400153&bindingType=&params=GlvVfC69WW22IWEtPMQeNPelPJSh4aaN6TPudbnbA59bfTIvGAmtrhRGe1sJEu9YKqhaYm8Fh6kcTUC6WsKXXHNMM2ja4SpIC%2FmDY8mmps3vzmo4c5GdWJ1VOThBX8ljOu400ep3zvZGRaUzHOSLeZpJTbftBtqWpcmwCoiji3c3qFUoMWoABxJi8HlYNf%2FC5K%2BDjB3qDd15v39DtlzLnBE%2BsaZ8qicE03vlOe0jJyyFZUvxdp0Nm963%2BvaqIDRF9jNKnq4fSuekf9cVsW%2Fzk3dzbG206mQT94Yd%2FFOz7MYXVKR2ur%2B0q06Ru9ieqcZOjo%2B2EbB97ydn3yJeCxBH6g%3D%3D")
                .header("Cookie", "JSESSIONID=F50FF0BE69B51E4F2592E41500F09CA2; jsessionid=2jcligmgi6fh; LDX-SESSION1=ZWU1NmI3ZmItMzcwNC00NzRiLWJkZmEtNmRkOGRiMjllNDdm; JSESSIONID=28A2A3F27BC9CCFEE1B117C3A03C1D8B; LDXSRV=s2|Y98aw|Y98aw; BIGipServerldx_116=166794156.20480.0000; _wx_zj_ldx_session_id_=5354555757545652525a5457573d0d2a522508080a342051050a0b2104130f3b01042e214f28315b2d33; LDXSRV=s2|Y98b0|Y98aw")
                .entity("consNo", "3306120926333")
                .entity("startTime", DateFormatUtils.format(yesterday, "yyyy-MM-dd"))
                .entity("endTime", DateFormatUtils.format(today, "yyyy-MM-dd"))
                .entity("operateTime", DateFormatUtils.format(today, "yyyy-MM-dd HH:mm:ss"))
                .doPost()
                .result();
        List<Electricity> list = JSONObject.parseArray(result, Electricity.class);
        if (list.size() > 0) {
            Electricity item = list.get(0);
            String content = String.format("昨日用电:%s 峰:%s 谷:%s", item.getZXYGZ(), item.getZXYGZ2(), item.getZXYGZ4());
            wechatWorkClient.appText(content, TokenFactory.innerStore(TokenEnum.WECHAT_WORK_ALARM));
        }
    }


    @Data
    private static class Electricity {
        private String datetime;
        // 峰用电
        private String ZXYGZ2;
        // 谷用电
        private String ZXYGZ4;
        // 日用电
        private String ZXYGZ;

    }

}
