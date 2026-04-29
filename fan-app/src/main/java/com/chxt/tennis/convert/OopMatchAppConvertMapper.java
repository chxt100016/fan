package com.chxt.tennis.convert;

import com.chxt.client.tennistv.model.OopResponse;
import com.chxt.tennis.model.Match;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OopMatchAppConvertMapper {

    OopMatchAppConvertMapper INSTANCE = Mappers.getMapper(OopMatchAppConvertMapper.class);

    @Mapping(target = "tournamentId", expression = "java(String.valueOf(detail.getTournamentId()))")
    @Mapping(target = "round", expression = "java(detail.getRound() != null ? detail.getRound().getLongName() : null)")
    @Mapping(target = "playerId1", expression = "java(detail.getPlayerTeam1() != null ? detail.getPlayerTeam1().getPlayerId() : null)")
    @Mapping(target = "playerId2", expression = "java(detail.getPlayerTeam2() != null ? detail.getPlayerTeam2().getPlayerId() : null)")
    @Mapping(target = "playerName1", expression = "java(buildPlayerName(detail.getPlayerTeam1()))")
    @Mapping(target = "playerName2", expression = "java(buildPlayerName(detail.getPlayerTeam2()))")
    @Mapping(target = "score", expression = "java(buildScoreString(detail))")
    @Mapping(target = "status", expression = "java(convertOopStatus(detail.getStatus()))")
    @Mapping(target = "winnerId", source = "winningPlayerId")
    @Mapping(target = "matchTime", expression = "java(parseDateTime(detail.getMatchDate()))")
    @Mapping(target = "notBeforeTime", expression = "java(parseDateTime(detail.getNotBeforeISOTime()))")
    @Mapping(target = "notBeforeText", source = "notBeforeText")
    @Mapping(target = "source", constant = "oop")
    @Mapping(target = "matchId", source = "matchId")
    @Mapping(target = "courtName", source = "courtName")
    @Mapping(target = "drawType", source = "associationCode")
    @Mapping(target = "roundName", ignore = true)
    @Mapping(target = "setsScore", ignore = true)
    @Mapping(target = "sets", ignore = true)
    Match toMatch(OopResponse.MatchDetail detail);

    default String buildPlayerName(OopResponse.PlayerTeam team) {
        if (team == null) return null;
        StringBuilder sb = new StringBuilder();
        if (team.getPlayerFirstNameFull() != null) {
            sb.append(team.getPlayerFirstNameFull());
        } else if (team.getPlayerFirstName() != null) {
            sb.append(team.getPlayerFirstName());
        }
        if (team.getPlayerLastName() != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(team.getPlayerLastName());
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    default String buildScoreString(OopResponse.MatchDetail detail) {
        if (detail.getPlayerTeam1() == null || detail.getPlayerTeam2() == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        java.util.List<OopResponse.SetScore> sets1 = detail.getPlayerTeam1().getSets();
        java.util.List<OopResponse.SetScore> sets2 = detail.getPlayerTeam2().getSets();
        if (sets1 != null && sets2 != null) {
            for (int i = 0; i < Math.max(sets1.size(), sets2.size()); i++) {
                if (i > 0) sb.append(" ");
                String score1 = i < sets1.size() ? sets1.get(i).getSetScore() : "0";
                String score2 = i < sets2.size() ? sets2.get(i).getSetScore() : "0";
                sb.append(score1).append("-").append(score2);
            }
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    default String convertOopStatus(String status) {
        if (status == null) return null;
        return switch (status) {
            case "F" -> "finished";
            case "L" -> "live";
            case "S" -> "scheduled";
            case "C" -> "cancelled";
            default -> status;
        };
    }

    default java.time.LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return null;
        try {
            return java.time.LocalDateTime.parse(dateTimeStr);
        } catch (Exception e) {
            return null;
        }
    }
}
