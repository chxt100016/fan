package com.chxt.domain.transaction.model.vo;

import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.transaction.model.entity.TransactionLog;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class TransactionTagVO {

    private String type;

    private String tag;

    private String description;




    public static List<TransactionTagVO> of(TransactionLog log) {
        List<TransactionTagVO> res = new ArrayList<>();

        LogDescVO desc = log.getDescription();
        for (Map.Entry<String, String> entry : desc.getData().entrySet()) {
            TransactionTagVO item = new TransactionTagVO(TransactionEnums.TagType.SYSTEM.getCode(), entry.getValue(), entry.getKey());
            res.add(item);
        }

        return res;
    }

}
