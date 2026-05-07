-- ============================================================
-- 添加 year 字段，唯一键从 tournament_id 改为 (tournament_id, year)
-- ============================================================

-- tennis_tournament
ALTER TABLE tennis_tournament ADD COLUMN year INT NOT NULL DEFAULT 2026 COMMENT '赛事年份' AFTER tournament_id;
ALTER TABLE tennis_tournament DROP INDEX uk_tennis_tournament_tournament_id;
ALTER TABLE tennis_tournament ADD UNIQUE KEY uk_tennis_tournament_tournament_year (tournament_id, year);
ALTER TABLE tennis_tournament ADD INDEX idx_tennis_tournament_year (year);
UPDATE tennis_tournament SET year = YEAR(start_date) WHERE year = 2026 AND start_date IS NOT NULL;

-- tennis_draw
ALTER TABLE tennis_draw ADD COLUMN year INT NOT NULL DEFAULT 2026 COMMENT '赛事年份' AFTER tournament_id;
ALTER TABLE tennis_draw DROP INDEX uk_tennis_draw_tournament_type;
ALTER TABLE tennis_draw ADD UNIQUE KEY uk_tennis_draw_tournament_year_type (tournament_id, year, draw_type);

-- tennis_match
ALTER TABLE tennis_match ADD COLUMN year INT COMMENT '赛事年份' AFTER tournament_id;
ALTER TABLE tennis_match ADD INDEX idx_tennis_match_tournament_year (tournament_id, year);

-- tennis_tournament_entry
ALTER TABLE tennis_tournament_entry ADD COLUMN year INT NOT NULL DEFAULT 2026 COMMENT '赛事年份' AFTER tournament_id;
ALTER TABLE tennis_tournament_entry DROP INDEX uk_tennis_entry_player_draw;
ALTER TABLE tennis_tournament_entry ADD UNIQUE KEY uk_tennis_entry_player_draw_year (tournament_id, year, player_id, draw_type);
