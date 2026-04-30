package com.chxt.tennis.convert;

import com.chxt.client.tennistv.model.MatchesResponse;
import com.chxt.db.tennis.entity.TennisMatchPO;
import com.chxt.tennis.model.Match;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {PlayerAppConvertMapper.class})
public interface MatchAppConvertMapper {

    MatchAppConvertMapper INSTANCE = Mappers.getMapper(MatchAppConvertMapper.class);

    @Mapping(target = "tournamentId", expression = "java(info.getTournamentId() != null ? String.valueOf(info.getTournamentId()) : null)")
    @Mapping(target = "player1Id", expression = "java(info.getPlayerTeam1() != null ? info.getPlayerTeam1().getPlayerId() : null)")
    @Mapping(target = "player2Id", expression = "java(info.getPlayerTeam2() != null ? info.getPlayerTeam2().getPlayerId() : null)")
    @Mapping(target = "playerName1", expression = "java(buildPlayerName(info.getPlayerTeam1()))")
    @Mapping(target = "playerName2", expression = "java(buildPlayerName(info.getPlayerTeam2()))")
    @Mapping(target = "status", expression = "java(convertLiveMatchStatus(info.getStatus()))")
    @Mapping(target = "scheduledAt", expression = "java(parseDateTime(info.getMatchDate()))")
    @Mapping(target = "roundName", ignore = true)
    @Mapping(target = "drawId", ignore = true)
    @Mapping(target = "roundNumber", ignore = true)
    @Mapping(target = "winnerId", ignore = true)
    @Mapping(target = "court", ignore = true)
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "endedAt", ignore = true)
    @Mapping(target = "durationMinutes", ignore = true)
    @Mapping(target = "sets", ignore = true)
    Match toMatch(MatchesResponse.MatchInfo info);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    TennisMatchPO toMatchPO(Match match);

    List<TennisMatchPO> toMatchPOList(List<Match> matches);

    default String buildPlayerName(MatchesResponse.PlayerTeam team) {
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

    default String convertLiveMatchStatus(String status) {
        if (status == null) return null;
        return switch (status) {
            case "C" -> "completed";
            case "L" -> "live";
            case "U" -> "upcoming";
            case "S" -> "scheduled";
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
