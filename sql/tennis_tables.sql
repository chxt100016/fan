-- ============================================================
-- 网球赛程数据库建表语句（MySQL 8.0+）
-- ============================================================

CREATE TABLE tennis_player (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    nationality   CHAR(3)      NOT NULL COMMENT 'ISO 3166-1 alpha-3，如 CHN / USA',
    birth_date    DATE,
    gender        CHAR(1)      NOT NULL COMMENT 'M / F',
    ranking       INT          COMMENT '当前排名，NULL 表示未排名',
    hand          VARCHAR(10)  NOT NULL DEFAULT 'UNKNOWN' COMMENT 'RIGHT / LEFT / UNKNOWN',
    create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_tennis_player_name    (last_name, first_name),
    INDEX idx_tennis_player_ranking (atp_ranking),
    INDEX idx_tennis_player_nation  (nationality)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='球员基础信息';


CREATE TABLE tennis_tournament (
id           BIGINT        NOT NULL AUTO_INCREMENT,
name         VARCHAR(100)  NOT NULL COMMENT '赛事名称，如 Wimbledon',
tour VARCHAR(10) NOT NULL DEFAULT 'ATP' COMMENT 'ATP / WTA / ITF/ Grand',
category     VARCHAR(20)   NOT NULL COMMENT '',
surface      VARCHAR(10)   NOT NULL COMMENT '',
city         VARCHAR(50)   NOT NULL,
country      VARCHAR(16)   NOT NULL COMMENT '',
prize_money  INT           COMMENT '总奖金（USD）',
prize_money_text  VARCHAR(50)    COMMENT '总奖金（USD）',
start_date   DATE          NOT NULL,
end_date     DATE          NOT NULL,
create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
update_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (id),
INDEX idx_tennis_tournament_date     (start_date, end_date),
INDEX idx_tennis_tournament_status   (status),
INDEX idx_tennis_tournament_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事主信息';


CREATE TABLE tennis_draw (
id            BIGINT      NOT NULL AUTO_INCREMENT,
tournament_id BIGINT      NOT NULL,
draw_type     VARCHAR(10) NOT NULL COMMENT 'MS / WS / MD / WD / XD',
size          INT         NOT NULL COMMENT '签表人数：32 / 64 / 128',
total_rounds  INT         NOT NULL COMMENT '总轮数，由 size 决定',
create_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
update_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (id),
UNIQUE KEY uk_tennis_draw_tournament_type (tournament_id, draw_type),
INDEX idx_tennis_draw_tournament (tournament_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签表（赛事下的具体项目）';


CREATE TABLE tennis_tournament_entry (
id            BIGINT      NOT NULL AUTO_INCREMENT,
tournament_id BIGINT      NOT NULL,
player_id     BIGINT      NOT NULL,
draw_type     VARCHAR(10) NOT NULL COMMENT '对应哪个项目签表',
seed          SMALLINT    COMMENT '种子号，NULL 表示非种子',
entry_type    VARCHAR(10) NOT NULL DEFAULT 'DIRECT' COMMENT 'DIRECT / WILDCARD / QUALIFIER / LUCKY_LOSER',
status        VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED' COMMENT 'CONFIRMED / WITHDRAWN / RETIRED',
create_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
update_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (id),
UNIQUE KEY uk_tennis_entry_player_draw (tournament_id, player_id, draw_type),
INDEX idx_tennis_entry_tournament     (tournament_id),
INDEX idx_tennis_entry_player         (player_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='球员报名信息';


CREATE TABLE tennis_match (
id               BIGINT      NOT NULL AUTO_INCREMENT,
draw_id          BIGINT      NOT NULL,
round_number     TINYINT     NOT NULL COMMENT '轮次序号：1=首轮，7=决赛（128签）',
round_name       VARCHAR(10) NOT NULL COMMENT 'R128 / R64 / R32 / R16 / QF / SF / F',
player1_id       BIGINT      COMMENT '未确定对阵时允许为 NULL（待晋级）',
player2_id       BIGINT      COMMENT '同上',
winner_id        BIGINT      COMMENT '比赛结束前为 NULL',
scheduled_at     DATETIME    COMMENT '计划开赛时间',
started_at       DATETIME    COMMENT '实际开始时间',
ended_at         DATETIME    COMMENT '实际结束时间',
court            VARCHAR(50) COMMENT '场地名称，如 Centre Court',
status           VARCHAR(20) NOT NULL DEFAULT '' COMMENT '',
duration_minutes SMALLINT    COMMENT '比赛时长（分钟）',
create_time       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
update_time       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (id),
INDEX idx_tennis_match_draw        (draw_id, round_number),
INDEX idx_tennis_match_player1     (player1_id),
INDEX idx_tennis_match_player2     (player2_id),
INDEX idx_tennis_match_status_time (status, scheduled_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='具体比赛场次';


CREATE TABLE tennis_set_score (
id          BIGINT   NOT NULL AUTO_INCREMENT,
match_id    BIGINT   NOT NULL,
set_number  TINYINT  NOT NULL COMMENT '第几盘：1 / 2 / 3 ...',
p1_games    TINYINT  NOT NULL DEFAULT 0 COMMENT 'player1 局数',
p2_games    TINYINT  NOT NULL DEFAULT 0 COMMENT 'player2 局数',
p1_tiebreak TINYINT  COMMENT '抢七分数，NULL 表示该盘无抢七',
p2_tiebreak TINYINT  COMMENT '抢七分数，NULL 表示该盘无抢七',
create_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
update_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (id),
UNIQUE KEY uk_tennis_set_match_number (match_id, set_number),
INDEX idx_tennis_set_match (match_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每盘比分详情';