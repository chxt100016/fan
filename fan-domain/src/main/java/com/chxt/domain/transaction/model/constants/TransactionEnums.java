package com.chxt.domain.transaction.model.constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.chxt.domain.transaction.component.RecordParserStrategy;
import com.chxt.domain.transaction.component.impl.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

public class TransactionEnums {

    @AllArgsConstructor
    @Getter
    public enum Channel {
        
        CMB_CREDIT("cmb_credit", "招商信用卡", CmbCreditParser.class),
        CMB_BANK("cmb_bank", "招商储蓄卡", CmbBankParser.class),
        ALI_PAY("ali_pay", "支付宝", AliPayParser.class),
        WECHAT_PAY("wechat_pay", "微信支付", WechatPayParser.class),
//        CGBC_CREDIT("cgbc_credit", "广发信用卡", CgbcCreditParser.class),
        ;

        private final String code;

        private final String name;

		private final Class<? extends RecordParserStrategy<?>> clazz;

		@SneakyThrows
		public static Map<String, RecordParserStrategy<?>> getAllParser() {
			Map<String, RecordParserStrategy<?>> res = new HashMap<>();
			for (Channel values : Channel.values()) {
				RecordParserStrategy<?> newInstance = values.getClazz().getDeclaredConstructor().newInstance();
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
        INCOME("income", "收入"),
        EXPENSE("expense", "支出"),
        ;

        private final String code;

        private final String name;

        public static Type of(String code) {
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

    @AllArgsConstructor
    @Getter
    public enum TagType {
        SYSTEM("system", "系统"),
        AI_GENERATED("ai_generated", "AI生成"),
        CATEGORY("category", "类型分类"),

        ;

        private final String code;
        private final String name;
    }

}
