package ru.practicum.explorewithme.statsserver.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.statsserver.stats.db.Stats;
import ru.practicum.explorewithme.statsserver.stats.db.StatsRepository;
import ru.practicum.explorewithme.statsserver.stats.dto.HitsDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public Stats create(Stats stats) {
        return statsRepository.save(stats);
    }

    @Override
    public List<HitsDto> get(@Nullable List<String> uris, Boolean unique, LocalDateTime start, LocalDateTime end, String appName) {
        List<HitsDto> hits = statsRepository.getHits(uris, unique, start, end, appName);
        hits.forEach(h -> h.setApp(appName));
        return hits;
    }
}
