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
    public enum Channel {
        
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
			for (Channel values : Channel.values()) {
				MailParserStrategy<?> newInstance = values.getClazz().getDeclaredConstructor().newInstance();
				res.put(values.getCode(), newInstance);
			}
			return res;
		}

        public static String getNameByCode(String code) {
            for (Channel values : Channel.values()) {
                if (values.getCode().equals(code)) {
                    return values.getName();
                }
            }
            return null;
        }

    }

    @AllArgsConstructor
    @Getter
    public enum Type {
        INCOME("INCOME", "收入"),
        EXPENSE("EXPENSE", "支出"),
        ;

        private final String code;

        private final String name;

        public static Type getByCode(String code) {
            return Arrays.stream(Type.values())
                .filter(type -> type.getCode().equals(code))
                .findFirst()
                .orElse(null);
        }
    }

    @AllArgsConstructor
    @Getter
    public enum Currency {
        CNY("CNY", "人民币"),

        ;

        private final String code;

        private final String name;
    }

    @AllArgsConstructor
    @Getter
    public enum Relation {
        ORIGINAL("original", "正向"),
        REFUND("refund", "退款")

        ;
        private final String code;
        private final String name;
    }

}
