
	 host = "imap.qq.com";  // QQ邮箱的IMAP服务器
    username = "546555918@qq.com";  // 你的QQ邮箱地址
    password = "nnfjkmehqypgbbhc";  // 你的QQ邮箱授权码（不是登录密码）
    private String startDateStr = "2025-05-25";  // 开始日期，格式为yyyy-MM-dd
	private List<String> parserCode;



``` sql
CREATE TABLE `user_mail` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` VARCHAR(64) NOT NULL COMMENT '用户ID',
    `host` VARCHAR(255) NOT NULL COMMENT '邮件服务器地址',
    `alias` VARCHAR(255) DEFAULT NULL COMMENT '邮箱别名',
    `username` VARCHAR(255) NOT NULL COMMENT '邮箱账号',
    `password` VARCHAR(255) NOT NULL COMMENT '邮箱密码',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户邮箱配置表';

-- message_box 站内信 mission
CREATE TABLE `message_box` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `unique_no` VARCHAR(64) DEFAULT NULL COMMENT '唯一编号',
  `user_id` VARCHAR(64) DEFAULT NULL COMMENT '用户ID',
  `title` VARCHAR(255) DEFAULT NULL COMMENT '标题',
  `message` TEXT DEFAULT NULL COMMENT '消息内容',
  `extra_data` TEXT DEFAULT NULL COMMENT '额外数据',
  `answer` TEXT DEFAULT NULL COMMENT '答复/回复',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_unique_no` (`unique_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息箱表';

-- TransactionChannelLogPO 对应表 transaction_channel_log
CREATE TABLE `transaction_channel_log` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` VARCHAR(64) DEFAULT NULL COMMENT '用户ID',
  `channel` VARCHAR(255),
  `operation_date` VARCHAR(255),
  `date` DATETIME,
  `count` INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- TransactionChannelPO 对应表 transaction_channel
CREATE TABLE `transaction_channel` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `transaction_id` BIGINT,
  `channel` VARCHAR(255),
  `parent` VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- TransactionLogPO 对应表 transaction_log
CREATE TABLE `transaction_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` VARCHAR(64) DEFAULT NULL COMMENT '用户ID',
  `date` DATE,
  `amount` DECIMAL(19,2),
  `currency` VARCHAR(255),
  `type` VARCHAR(255),
  `method` VARCHAR(255),
  `channel` VARCHAR(255),
  `description` VARCHAR(255),
  `log_id` VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- TransationTagPO 对应表 transaction_tag
CREATE TABLE `transaction_tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `transaction_id` BIGINT,
  `type` VARCHAR(255),
  `tag` VARCHAR(255),
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- TransationPO 对应表 transaction
CREATE TABLE `transaction` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `amount` VARCHAR(255),
  `currency` VARCHAR(255),
  `type` VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```