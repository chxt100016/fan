package com.chxt.domain.transaction.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class TransactionEnums {

    @AllArgsConstructor
    @Getter
    public enum CHANNEL {
        
        CMB_CREDIT("cmb_credit", "招商银行信用卡"),
        ALI_PAY("ali_pay", "支付宝"),
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
