package ru.practicum.explorewithme.statsserver.stats.db;

import org.springframework.lang.Nullable;
import ru.practicum.explorewithme.statsserver.stats.dto.HitsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository {
    Stats save(Stats stats);

    List<HitsDto> getHits(@Nullable List<String> uris, Boolean unique, LocalDateTime start, LocalDateTime end, String appName);
}
