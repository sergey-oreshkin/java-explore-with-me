package ru.practicum.explorewithme.statsserver.stats.db;

import ru.practicum.explorewithme.statsserver.stats.dto.HitsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository {
    Stats save(Stats stats);

    List<HitsDto> getHits(List<String> uris, Boolean unique, LocalDateTime start, LocalDateTime end, String appName);
}
