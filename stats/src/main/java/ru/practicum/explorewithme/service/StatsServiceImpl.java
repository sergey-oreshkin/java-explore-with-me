package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.stats.db.Stats;
import ru.practicum.explorewithme.stats.db.StatsRepository;
import ru.practicum.explorewithme.stats.dto.HitsDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl {

    private final StatsRepository statsRepository;

    public Stats create(Stats stats) {
        return statsRepository.save(stats);
    }

    public List<HitsDto> get(List<String> uris, Boolean unique, LocalDateTime start, LocalDateTime end, String appName) {
        return statsRepository.getHits(uris, unique, start, end, appName);
    }
}
