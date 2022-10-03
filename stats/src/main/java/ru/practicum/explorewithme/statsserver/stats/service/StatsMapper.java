package ru.practicum.explorewithme.statsserver.stats.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.statsserver.stats.db.Stats;
import ru.practicum.explorewithme.statsserver.stats.dto.StatsDto;

@Mapper(componentModel = "spring")
public interface StatsMapper {

    @Mapping(target = "created", source = "timestamp")
    Stats toEntity(StatsDto statsDto);

    @Mapping(target = "timestamp", source = "created")
    StatsDto toDto(Stats stats);

//    List<StatsOutputDto> toOutput(List<Stats> stats);
}
