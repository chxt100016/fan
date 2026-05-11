-- 为 tennis_tournament_entry 表添加 draw_id 字段，关联 tennis_draw 表
ALTER TABLE tennis_tournament_entry
    ADD COLUMN draw_id BIGINT COMMENT '签表ID，关联 tennis_draw 表' AFTER player_id;

-- 添加索引
ALTER TABLE tennis_tournament_entry
    ADD INDEX idx_tennis_entry_draw (draw_id);
