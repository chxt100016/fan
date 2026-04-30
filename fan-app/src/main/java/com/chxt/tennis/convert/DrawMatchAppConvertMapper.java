package com.chxt.tennis.convert;

import com.chxt.client.tennistv.model.DrawsResponse;
import com.chxt.tennis.model.Match;
import com.chxt.tennis.model.SetScore;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Mapper
public interface DrawMatchAppConvertMapper {

    DrawMatchAppConvertMapper INSTANCE = Mappers.getMapper(DrawMatchAppConvertMapper.class);

    @Mapping(target = "matchId", expression = "java(getMatchId(fixture))")
    @Mapping(target = "player1Id", expression = "java(getPlayer1Id(fixture))")
    @Mapping(target = "player2Id", expression = "java(getPlayer2Id(fixture))")
    @Mapping(target = "playerName1", expression = "java(buildFullName(getPlayer1(fixture)))")
    @Mapping(target = "playerName2", expression = "java(buildFullName(getPlayer2(fixture)))")
    @Mapping(target = "winnerId", expression = "java(getWinnerId(fixture))")
    @Mapping(target = "status", expression = "java(convertDrawStatus(fixture.getPulseStatus()))")
    @Mapping(target = "startedAt", expression = "java(parseMatchDate(fixture))")
    @Mapping(target = "endedAt", expression = "java(parseMatchDate(fixture))")
    @Mapping(target = "durationMinutes", expression = "java(parseDuration(fixture.getResult()))")
    @Mapping(target = "court", expression = "java(getCourtName(fixture))")
    @Mapping(target = "roundName", ignore = true)
    @Mapping(target = "tournamentId", ignore = true)
    @Mapping(target = "drawId", ignore = true)
    @Mapping(target = "roundNumber", ignore = true)
    @Mapping(target = "scheduledAt", ignore = true)
    @Mapping(target = "sets", expression = "java(parseSetResults(fixture))")
    @Mapping(target = "description", expression = "java(fixture.getMetadata().getDescription())")
    Match toMatch(DrawsResponse.Fixture fixture);

    default Long parseLong(Object value) {
        if (value == null) return null;
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    default String getMatchId(DrawsResponse.Fixture fixture) {
        if (fixture == null) return null;
        // 优先从 MatchInfo 获取 MatchId
        if (fixture.getMatch() != null && fixture.getMatch().getMatchId() != null) {
            return fixture.getMatch().getMatchId();
        }
        // 从 ResultInfo 获取 MatchCode
        if (fixture.getResult() != null && fixture.getResult().getMatchCode() != null) {
            String matchCode = fixture.getResult().getMatchCode();
            if (!"scheduled".equals(matchCode) && !"live".equals(matchCode) && !"finished".equals(matchCode)) {
                return matchCode;
            }
        }
        // 从 Fixture 的 MatchCode
        if (fixture.getMatchCode() != null) {
            String matchCode = fixture.getMatchCode();
            if (!"scheduled".equals(matchCode) && !"live".equals(matchCode) && !"finished".equals(matchCode)) {
                return matchCode;
            }
        }
        return null;
    }

    default String getPlayer1Id(DrawsResponse.Fixture fixture) {
        if (fixture.getResult() == null) return null;
        if (fixture.getResult().getTeamTop() == null) return null;
        if (fixture.getResult().getTeamTop().getPlayer() == null) return null;
        return fixture.getResult().getTeamTop().getPlayer().getPlayerId();
    }

    default String getPlayer2Id(DrawsResponse.Fixture fixture) {
        if (fixture.getResult() == null) return null;
        if (fixture.getResult().getTeamBottom() == null) return null;
        if (fixture.getResult().getTeamBottom().getPlayer() == null) return null;
        return fixture.getResult().getTeamBottom().getPlayer().getPlayerId();
    }

    default String getWinnerId(DrawsResponse.Fixture fixture) {
        // 从 MatchInfo.WinningPlayerId 获取胜利者球员 ID
        if (fixture.getMatch() != null && fixture.getMatch().getWinningPlayerId() != null) {
            return fixture.getMatch().getWinningPlayerId();
        }
        return null;
    }

    default DrawsResponse.PlayerInfo getPlayer1(DrawsResponse.Fixture fixture) {
        if (fixture.getResult() == null) return null;
        if (fixture.getResult().getTeamTop() == null) return null;
        return fixture.getResult().getTeamTop().getPlayer();
    }

    default DrawsResponse.PlayerInfo getPlayer2(DrawsResponse.Fixture fixture) {
        if (fixture.getResult() == null) return null;
        if (fixture.getResult().getTeamBottom() == null) return null;
        return fixture.getResult().getTeamBottom().getPlayer();
    }

    default String buildFullName(DrawsResponse.PlayerInfo info) {
        if (info == null) return null;
        StringBuilder sb = new StringBuilder();
        if (info.getFirstName() != null) sb.append(info.getFirstName());
        if (info.getLastName() != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(info.getLastName());
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    default String convertDrawStatus(String pulseStatus) {
        if (pulseStatus == null) return "scheduled";
        return switch (pulseStatus) {
            case "C" -> "finished";
            case "L" -> "live";
            case "U" -> "scheduled";
            default -> "scheduled";
        };
    }

    default LocalDateTime parseMatchDate(DrawsResponse.Fixture fixture) {
        if (fixture.getMatch() == null || fixture.getMatch().getMatchDate() == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(fixture.getMatch().getMatchDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return null;
        }
    }

    default Integer parseDuration(DrawsResponse.ResultInfo result) {
        if (result == null || result.getMatchTime() == null) {
            return null;
        }
        // 格式: "01:26:33" -> 分钟数
        try {
            String[] parts = result.getMatchTime().split(":");
            if (parts.length == 3) {
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                return hours * 60 + minutes;
            }
        } catch (Exception e) {
            // 解析失败返回 null
        }
        return null;
    }

    default String getCourtName(DrawsResponse.Fixture fixture) {
        if (fixture.getMatch() == null) return null;
        return fixture.getMatch().getCourtName();
    }

    default List<SetScore> parseSetResults(DrawsResponse.Fixture fixture) {
        if (fixture.getResult() == null || fixture.getResult().getSetResults() == null) {
            return null;
        }
        List<SetScore> sets = new ArrayList<>();
        for (DrawsResponse.SetResult sr : fixture.getResult().getSetResults()) {
            SetScore setScore = SetScore.builder()
                    .setNumber(sr.getSetNumber())
                    .p1Games(sr.getGamesA())
                    .p2Games(sr.getGamesB())
                    .p1Tiebreak(sr.getTiebreakA())
                    .p2Tiebreak(sr.getTiebreakB())
                    .build();
            sets.add(setScore);
        }
        return sets.isEmpty() ? null : sets;
    }
}
