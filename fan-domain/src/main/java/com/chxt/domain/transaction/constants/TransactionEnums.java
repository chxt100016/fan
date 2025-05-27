package com.chxt.domain.transaction.constants;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class TransactionEnums {

    @AllArgsConstructor
    @Getter
    public enum CHANNEL {
        
        CMB_CREDIT("cmb_credit", "招商信用卡"),
        ALI_PAY("ali_pay", "支付宝"),
        WECHAT_PAY("wechat_pay", "微信支付"),
        CGBC_CREDIT("cgbc_credit", "广发信用卡"),
        ;

        private final String code;

        private final String name;

    }

    @AllArgsConstructor
    @Getter
    public enum TYPE {
        INCOME("income", "收入"),
        EXPENSE("expense", "支出"),
        ;

        private final String code;

        private final String name;

        public static TYPE getByCode(String code) {
            return Arrays.stream(TYPE.values())
                .filter(type -> type.getCode().equals(code))
                .findFirst()
                .orElse(null);
        }
    }

    @AllArgsConstructor
    @Getter
    public enum CURRENCY {
        CNY("CNY", "人民币"),

        ;

        private final String code;

        private final String name;
    }

}
