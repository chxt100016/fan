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


	private String host = "imap.qq.com";  // QQ邮箱的IMAP服务器
    private String username = "546555918@qq.com";  // 你的QQ邮箱地址
    private String password = "nnfjkmehqypgbbhc";  // 你的QQ邮箱授权码（不是登录密码）
    private String startDateStr = "2025-05-25";  // 开始日期，格式为yyyy-MM-dd
	private List<String> parserCode;

}
