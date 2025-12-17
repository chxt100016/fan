package com.chxt.domain.transaction.model.constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.chxt.domain.transaction.component.MailParserStrategy;
import com.chxt.domain.transaction.component.impl.AliPayParser;
import com.chxt.domain.transaction.component.impl.CgbcCreditParser;
import com.chxt.domain.transaction.component.impl.CmbCreditParser;
import com.chxt.domain.transaction.component.impl.WechatPayParser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

public class TransactionEnums {

    @AllArgsConstructor
    @Getter
    public enum CHANNEL {
        
        CMB_CREDIT("CMB_CREDIT", "招商信用卡", CmbCreditParser.class),
        ALI_PAY("ALI_PAY", "支付宝", AliPayParser.class),
        WECHAT_PAY("WECHAT_PAY", "微信支付", WechatPayParser.class),
        CGBC_CREDIT("CGBC_CREDIT", "广发信用卡", CgbcCreditParser.class),
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

        public static String getNameByCode(String code) {
            for (CHANNEL values : CHANNEL.values()) {
                if (values.getCode().equals(code)) {
                    return values.getName();
                }
            }
            return null;
        }

    }

    @AllArgsConstructor
    @Getter
    public enum TYPE {
        INCOME("INCOME", "收入"),
        EXPENSE("EXPENSE", "支出"),
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
