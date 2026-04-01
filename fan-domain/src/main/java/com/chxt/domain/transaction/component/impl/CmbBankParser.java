package com.chxt.domain.transaction.component.impl;

import com.chxt.domain.transaction.component.RecordParserContext;
import com.chxt.domain.transaction.component.RecordParserStrategy;
import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.transaction.model.vo.LogDescVO;
import com.chxt.domain.utils.Pdf;
import com.chxt.domain.utils.Zip;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

public class CmbBankParser  implements RecordParserStrategy<CmbBankParser.CmbBankItem> {

    private static final String FROM = "95555@message.cmbchina.com";

    private static final String SUBJECT = "招商银行交易流水";

    private static final String PDF_PAGE_PARSE_AFTER = "Counter Party";

    private static final String LINE_START = "^\\d{4}-\\d{2}-\\d{2}.*";

    private static final String SEPARATOR = "@@@";


    @Override
    public String getFrom() {
        return FROM;
    }

    @Override
    public String getSubject() {
        return SUBJECT;
    }

    @Override
    public void parse(RecordParserContext<CmbBankItem> context) {

        String password = context.getPassword();

        // 解压ZIP文件
        Zip zip = new Zip(context.getAttachment(), password);
        Pdf pdf = new Pdf(zip.getOne());
        List<List<String>> linesWithPage = pdf.linesWithPage();
        List<CmbBankItem> res = new ArrayList<>();
        for (List<String> lines : linesWithPage) {
            res.addAll(this.parsePdfPage(lines));
        }

        String String = linesWithPage.get(0).get(3);
        String[] split = String.split(" -- ");
        context.putExt("a", split[0]);
        context.putExt("b", split[1]);

        context.setData(res);
    }

    private List<CmbBankItem> parsePdfPage(List<String> lines) {
        // 找到起始位置，跳过 PARSE_AFTER 行本身
        int startIndex = IntStream.range(0, lines.size())
                .filter(i -> lines.get(i).contains(PDF_PAGE_PARSE_AFTER))
                .findFirst()
                .orElse(-1);

        if (startIndex == -1) return List.of();

        List<String> fixed = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        for (String line : lines.subList(startIndex + 1, lines.size())) {
            if (line.matches(LINE_START)) {
                if (!sb.isEmpty()) fixed.add(sb.toString());
                sb = new StringBuilder(String.join(SEPARATOR, line.trim().split("\\s+")));
            } else {
                sb.append(line);
            }
        }

        if (!sb.isEmpty()) fixed.add(sb.toString());

        return fixed.stream().map(CmbBankItem::new).toList();
    }

    @Override
    public String getChannel() {
        return TransactionEnums.Channel.CMB_BANK.getCode();
    }

    @Override
    @SneakyThrows
    public Date getTransactionStartDate(RecordParserContext<CmbBankItem> context) {
        return DateUtils.parseDate(context.getExt("a"), "yyyy-MM-dd");
    }

    @Override
    @SneakyThrows
    public Date getTransactionEndDate(RecordParserContext<CmbBankItem> context) {
        return DateUtils.parseDate(context.getExt("b"), "yyyy-MM-dd");
    }

    @Override
    @SneakyThrows
    public Date getDate(CmbBankItem data) {
        return DateUtils.parseDate(data.getDate(), "yyyy-MM-dd");
    }

    @Override
    public BigDecimal getAmount(CmbBankItem data) {
        String amountStr = data.getTransactionAmount().replaceAll("-", "").replaceAll(",", "");
        return new BigDecimal(amountStr);
    }

    @Override
    public String getCurrency(CmbBankItem data) {
        return data.getCurrency();
    }

    @Override
    public String getType(CmbBankItem data) {
        return data.getTransactionAmount().startsWith("-") ? TransactionEnums.Type.EXPENSE.getCode() :  TransactionEnums.Type.INCOME.getCode();
    }

    @Override
    public String getMethod(CmbBankItem data) {
        return null;
    }

    @Override
    public LogDescVO getDescription(CmbBankItem data) {
        return new LogDescVO()
                .put("交易摘要", data.getTransactionType());
    }

    @Override
    public String getCounterparty(CmbBankItem data) {
        return data.getCounterParty();
    }


    @Getter
    public static class CmbBankItem {

        // 日期
        private String Date;

        // 货币
        private String currency;

        // 交易金额
        private String transactionAmount;

        // 联机余额
        private String balance;

        // 交易摘要
        private String transactionType;

        // 对手信息
        private String counterParty;


        public CmbBankItem(String line) {
            List<String> data = Arrays.asList(line.split(SEPARATOR));
            if (!data.isEmpty()) {
                this.Date = data.get(0);
            }
            if (data.size() > 1) {
                this.currency = data.get(1);
            }
            if (data.size() > 2) {
                this.transactionAmount = data.get(2);
            }
            if (data.size() > 3) {
                this.balance = data.get(3);
            }
            if (data.size() > 4) {
                this.transactionType = data.get(4);
            }
            if (data.size() > 5) {
                this.counterParty = data.get(5);
            }
        }

    }


}
