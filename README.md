
``` sql
-- MissionPO 对应表 mission
CREATE TABLE `task` (
  `id` VARCHAR(255) NOT NULL PRIMARY KEY,
  `task_id` VARCHAR(255),
  `status` VARCHAR(255),
  `remark` VARCHAR(255),
  `data` VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- TransactionChannelLogPO 对应表 transaction_channel_log
CREATE TABLE `transaction_channel_log` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT,
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
  `tag` VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- TransationPO 对应表 transaction
CREATE TABLE `transaction` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `amount` VARCHAR(255),
  `currency` VARCHAR(255),
  `type` VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```