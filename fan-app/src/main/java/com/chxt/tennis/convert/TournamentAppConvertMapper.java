package com.chxt.tennis.convert;

import com.chxt.client.tennistv.model.MatchesResponse;
import com.chxt.db.tennis.entity.TennisTournamentPO;
import com.chxt.tennis.model.Tournament;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TournamentAppConvertMapper {

    TournamentAppConvertMapper INSTANCE = Mappers.getMapper(TournamentAppConvertMapper.class);

    @Mapping(target = "tournamentId", source = "id")
    @Mapping(target = "category", source = "type")
    @Mapping(target = "city", source = "info.city")
    @Mapping(target = "country", source = "location")
    @Mapping(target = "tour", constant = "ATP")
    @Mapping(target = "prizeMoneyText", expression = "java(info.getInfo() != null ? info.getInfo().getPrize() : null)")
    @Mapping(target = "prizeMoney", ignore = true)
    @Mapping(target = "status", constant = "active")
    @Mapping(target = "startDate", expression = "java(parseDate(info.getStart()))")
    @Mapping(target = "endDate", expression = "java(parseDate(info.getEnd()))")
    Tournament toTournament(MatchesResponse.TournamentInfo info);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    TennisTournamentPO toTournamentPO(Tournament tournament);

    List<TennisTournamentPO> toTournamentPOList(List<Tournament> tournaments);

    default java.time.LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            return java.time.LocalDate.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
}
