package com.chxt.domain.transaction.model.vo;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailParseParamVO {


    private String userId;
    private String startDateStr;  // 开始日期，格式为yyyy-MM-dd
	private List<String> channel;

}
