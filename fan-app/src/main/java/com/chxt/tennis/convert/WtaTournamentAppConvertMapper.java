package com.chxt.tennis.convert;

import com.chxt.client.wta.model.WtaTournamentsResponse;
import com.chxt.db.tennis.entity.TennisTournamentPO;
import com.chxt.tennis.model.Tournament;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface WtaTournamentAppConvertMapper {

    WtaTournamentAppConvertMapper INSTANCE = Mappers.getMapper(WtaTournamentAppConvertMapper.class);

    @Mapping(target = "tournamentId", expression = "java(item.getTournamentGroup() != null ? String.valueOf(item.getTournamentGroup().getId()) : null)")
    @Mapping(target = "year", source = "year")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "tour", constant = "WTA")
    // WTA 的 level 形如 "WTA 500"，只保留数字部分作为 category，对齐 ATP 风格
    @Mapping(target = "category", expression = "java(parseCategory(item.getTournamentGroup() != null ? item.getTournamentGroup().getLevel() : null))")
    @Mapping(target = "surface", source = "surface")
    @Mapping(target = "city", source = "city")
    @Mapping(target = "country", source = "country")
    @Mapping(target = "prizeMoney", expression = "java((int) item.getPrizeMoney())")
    @Mapping(target = "prizeMoneyText", expression = "java(item.getPrizeMoney() + \" \" + item.getPrizeMoneyCurrency())")
    // 接口 status: "past" 代表已结束，其余视为 active
    @Mapping(target = "status", expression = "java(\"past\".equals(item.getStatus()) ? \"completed\" : \"active\")")
    @Mapping(target = "startDate", expression = "java(parseDate(item.getStartDate()))")
    @Mapping(target = "endDate", expression = "java(parseDate(item.getEndDate()))")
    Tournament toTournament(WtaTournamentsResponse.TournamentItem item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    TennisTournamentPO toTournamentPO(Tournament tournament);

    List<TennisTournamentPO> toTournamentPOList(List<Tournament> tournaments);

    default String parseCategory(String level) {
        if (level == null) return null;
        String digits = level.replaceAll("\\D+", "");
        return digits.isEmpty() ? null : digits;
    }

    default LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
}
