package com.chxt.domain.transaction.model.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chxt.domain.transaction.model.constants.TransactionEnums.CHANNEL;
import com.chxt.domain.transaction.parser.MailParserStrategy;
import com.chxt.domain.transaction.parser.impl.AliPayParser;
import com.chxt.domain.transaction.parser.impl.CgbcCreditParser;
import com.chxt.domain.transaction.parser.impl.CmbCreditParser;
import com.chxt.domain.transaction.parser.impl.WechatPayParser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

public class TransactionEnums {

    @AllArgsConstructor
    @Getter
    public enum CHANNEL {
        
        CMB_CREDIT("cmb_credit", "招商信用卡", CmbCreditParser.class),
        ALI_PAY("ali_pay", "支付宝", AliPayParser.class),
        WECHAT_PAY("wechat_pay", "微信支付", WechatPayParser.class),
        CGBC_CREDIT("cgbc_credit", "广发信用卡", CgbcCreditParser.class),
        ;

        private final String code;

        private final String name;

		private final Class<? extends MailParserStrategy<?>> clazz;

		@SneakyThrows
		public static Map<String, MailParserStrategy<?>> getAllParser() {
			Map<String, MailParserStrategy<?>> res = new HashMap<>();
			for (CHANNEL values : CHANNEL.values()) {
				MailParserStrategy<?> newInstance = values.getClazz().getDeclaredConstructor().newInstance();
				res.put(values.getCode(), newInstance);
			}
			return res;
		}

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
