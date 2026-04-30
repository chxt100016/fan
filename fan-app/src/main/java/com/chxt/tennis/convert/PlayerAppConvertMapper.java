package com.chxt.tennis.convert;

import com.chxt.client.tennistv.model.DrawsResponse;
import com.chxt.client.tennistv.model.MatchesResponse;
import com.chxt.client.tennistv.model.OopResponse;
import com.chxt.db.tennis.entity.TennisPlayerPO;
import com.chxt.tennis.model.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PlayerAppConvertMapper {

    PlayerAppConvertMapper INSTANCE = Mappers.getMapper(PlayerAppConvertMapper.class);

    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "nationality", source = "playerCountryCode")
    Player toPlayer(MatchesResponse.PlayerTeam team);

    @Mapping(target = "countryCode", source = "nationality")
    @Mapping(target = "fullName", ignore = true)
    Player toPlayerFromDraw(DrawsResponse.PlayerInfo info);

    @Mapping(target = "countryCode", source = "playerCountryCode")
    @Mapping(target = "fullName", ignore = true)
    Player toPlayerFromOop(OopResponse.PlayerTeam team);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    TennisPlayerPO toPlayerPO(Player player);

    List<TennisPlayerPO> toPlayerPOList(List<Player> players);
}
