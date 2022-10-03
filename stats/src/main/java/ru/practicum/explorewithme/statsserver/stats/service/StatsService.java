package ru.practicum.explorewithme.statsserver.stats.service;

import ru.practicum.explorewithme.statsserver.stats.db.Stats;
import ru.practicum.explorewithme.statsserver.stats.dto.HitsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    Stats create(Stats stats);

    List<HitsDto> get(List<String> uris, Boolean unique, LocalDateTime start, LocalDateTime end, String appName);
}
