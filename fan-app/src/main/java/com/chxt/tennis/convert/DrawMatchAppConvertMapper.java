package com.chxt.tennis.convert;

import com.chxt.client.tennistv.model.DrawsResponse;
import com.chxt.tennis.model.Match;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DrawMatchAppConvertMapper {

    DrawMatchAppConvertMapper INSTANCE = Mappers.getMapper(DrawMatchAppConvertMapper.class);

    @Mapping(target = "matchId", source = "matchCode")
    @Mapping(target = "playerId1", expression = "java(getPlayerId1(fixture))")
    @Mapping(target = "playerId2", expression = "java(getPlayerId2(fixture))")
    @Mapping(target = "playerName1", expression = "java(buildFullName(getPlayer1(fixture)))")
    @Mapping(target = "playerName2", expression = "java(buildFullName(getPlayer2(fixture)))")
    @Mapping(target = "winnerId", expression = "java(fixture.getResult() != null ? String.valueOf(fixture.getResult().getWinner()) : null)")
    @Mapping(target = "status", expression = "java(convertDrawStatus(fixture.getPulseStatus()))")
    @Mapping(target = "source", constant = "draw")
    @Mapping(target = "round", ignore = true)
    @Mapping(target = "drawType", ignore = true)
    @Mapping(target = "tournamentId", ignore = true)
    @Mapping(target = "roundName", ignore = true)
    @Mapping(target = "score", ignore = true)
    @Mapping(target = "setsScore", ignore = true)
    @Mapping(target = "courtName", ignore = true)
    @Mapping(target = "notBeforeTime", ignore = true)
    @Mapping(target = "notBeforeText", ignore = true)
    @Mapping(target = "matchTime", ignore = true)
    @Mapping(target = "sets", ignore = true)
    Match toMatch(DrawsResponse.Fixture fixture);

    default String getPlayerId1(DrawsResponse.Fixture fixture) {
        if (fixture.getResult() == null) return null;
        if (fixture.getResult().getTeamTop() == null) return null;
        if (fixture.getResult().getTeamTop().getPlayer() == null) return null;
        return fixture.getResult().getTeamTop().getPlayer().getPlayerId();
    }

    default String getPlayerId2(DrawsResponse.Fixture fixture) {
        if (fixture.getResult() == null) return null;
        if (fixture.getResult().getTeamBottom() == null) return null;
        if (fixture.getResult().getTeamBottom().getPlayer() == null) return null;
        return fixture.getResult().getTeamBottom().getPlayer().getPlayerId();
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
}
