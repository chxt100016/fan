package com.chxt.tennis.convert;

import com.chxt.client.tennistv.model.OopResponse;
import com.chxt.tennis.model.Match;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OopMatchAppConvertMapper {

    OopMatchAppConvertMapper INSTANCE = Mappers.getMapper(OopMatchAppConvertMapper.class);

    @Mapping(target = "tournamentId", expression = "java(detail.getTournamentId() != null ? String.valueOf(detail.getTournamentId()) : null)")
    @Mapping(target = "player1Id", expression = "java(detail.getPlayerTeam1() != null ? detail.getPlayerTeam1().getPlayerId() : null)")
    @Mapping(target = "player2Id", expression = "java(detail.getPlayerTeam2() != null ? detail.getPlayerTeam2().getPlayerId() : null)")
    @Mapping(target = "playerName1", expression = "java(buildPlayerName(detail.getPlayerTeam1()))")
    @Mapping(target = "playerName2", expression = "java(buildPlayerName(detail.getPlayerTeam2()))")
    @Mapping(target = "status", expression = "java(convertOopStatus(detail.getStatus()))")
    @Mapping(target = "winnerId", expression = "java(detail.getWinningPlayerId())")
    @Mapping(target = "scheduledAt", expression = "java(parseDateTime(detail.getMatchDate()))")
    @Mapping(target = "court", source = "courtName")
    @Mapping(target = "roundName", expression = "java(detail.getRound() != null ? detail.getRound().getLongName() : null)")
    @Mapping(target = "roundNumber", ignore = true)
    @Mapping(target = "drawId", ignore = true)
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "endedAt", ignore = true)
    @Mapping(target = "durationMinutes", ignore = true)
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
