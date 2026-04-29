-- 网球数据采集系统表结构

-- 球员表
CREATE TABLE IF NOT EXISTS `tennis_player` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `player_id` varchar(64) NOT NULL COMMENT '球员唯一标识(API返回)',
  `first_name` varchar(128) DEFAULT NULL COMMENT '名',
  `last_name` varchar(128) DEFAULT NULL COMMENT '姓',
  `full_name` varchar(256) DEFAULT NULL COMMENT '全名',
  `nationality` varchar(64) DEFAULT NULL COMMENT '国籍',
  `country_code` varchar(8) DEFAULT NULL COMMENT '国家代码',
  `rank` int DEFAULT NULL COMMENT '排名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_player_id` (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='球员表';

-- 赛事表
CREATE TABLE IF NOT EXISTS `tennis_tournament` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tournament_id` varchar(64) NOT NULL COMMENT '赛事唯一标识(API返回)',
  `name` varchar(256) DEFAULT NULL COMMENT '赛事名称',
  `surface` varchar(32) DEFAULT NULL COMMENT '场地类型(clay/hard/grass/carpet)',
  `category` varchar(64) DEFAULT NULL COMMENT '赛事级别(GS/ATP1000/ATP500/ATP250等)',
  `city` varchar(128) DEFAULT NULL COMMENT '举办城市',
  `country` varchar(64) DEFAULT NULL COMMENT '举办国家',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `year` int DEFAULT NULL COMMENT '赛事年份',
  `status` varchar(32) DEFAULT 'active' COMMENT '赛事状态(active/completed)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tournament_id` (`tournament_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='赛事表';

-- 比赛表
CREATE TABLE IF NOT EXISTS `tennis_match` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `match_id` varchar(64) NOT NULL COMMENT '比赛唯一标识(API返回)',
  `tournament_id` varchar(64) NOT NULL COMMENT '所属赛事ID',
  `round` varchar(32) DEFAULT NULL COMMENT '轮次(R1/R2/R3/R4/QF/SF/F)',
  `round_name` varchar(64) DEFAULT NULL COMMENT '轮次名称(Round of 128等)',
  `draw_type` varchar(32) DEFAULT NULL COMMENT '签表类型(MS/MD/WS/WD)',
  `player1_id` varchar(64) DEFAULT NULL COMMENT '球员1 ID',
  `player2_id` varchar(64) DEFAULT NULL COMMENT '球员2 ID',
  `player1_name` varchar(256) DEFAULT NULL COMMENT '球员1姓名(冗余)',
  `player2_name` varchar(256) DEFAULT NULL COMMENT '球员2姓名(冗余)',
  `score` varchar(128) DEFAULT NULL COMMENT '比分(如 6-4 7-5)',
  `sets_score` varchar(128) DEFAULT NULL COMMENT '盘分(如 2-0)',
  `status` varchar(32) DEFAULT NULL COMMENT '比赛状态(scheduled/live/finished/cancelled)',
  `winner_id` varchar(64) DEFAULT NULL COMMENT '胜者ID',
  `court_name` varchar(128) DEFAULT NULL COMMENT '场地名称',
  `not_before_time` datetime DEFAULT NULL COMMENT '比赛不早于开始时间(NotBeforeISOTime)',
  `not_before_text` varchar(32) DEFAULT NULL COMMENT '比赛不早于开始时间(文本显示，如10:00)',
  `match_time` datetime DEFAULT NULL COMMENT '实际开始时间(API返回)',
  `source` varchar(32) NOT NULL COMMENT '数据来源(draw/oop/live)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_match_id` (`match_id`),
  KEY `idx_tournament_id` (`tournament_id`),
  KEY `idx_status` (`status`),
  KEY `idx_tournament_round` (`tournament_id`, `round`),
  KEY `idx_not_before_time` (`not_before_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='比赛表';

-- 比赛盘分表
CREATE TABLE IF NOT EXISTS `tennis_match_set` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `match_id` varchar(64) NOT NULL COMMENT '比赛ID',
  `set_no` int NOT NULL COMMENT '第几盘(1/2/3/4/5)',
  `player1_games` int DEFAULT NULL COMMENT '球员1赢的局数',
  `player2_games` int DEFAULT NULL COMMENT '球员2赢的局数',
  `player1_points` varchar(32) DEFAULT NULL COMMENT '球员1抢七分数',
  `player2_points` varchar(32) DEFAULT NULL COMMENT '球员2抢七分数',
  `tiebreak` tinyint(1) DEFAULT 0 COMMENT '是否抢七',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_match_set` (`match_id`, `set_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='比赛盘分表';
