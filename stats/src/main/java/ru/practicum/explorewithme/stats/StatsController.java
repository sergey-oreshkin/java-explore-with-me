package ru.practicum.explorewithme.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.service.StatsMapper;
import ru.practicum.explorewithme.service.StatsServiceImpl;
import ru.practicum.explorewithme.stats.db.Stats;
import ru.practicum.explorewithme.stats.dto.HitsDto;
import ru.practicum.explorewithme.stats.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsServiceImpl statsService;

    private final StatsMapper mapper;

    @PostMapping("/hit")
    public StatsDto hit(@RequestBody StatsDto statsDto) {
        Stats stats = mapper.toEntity(statsDto);
        return mapper.toDto(statsService.create(stats));
    }

    @GetMapping("/stats")
    public List<HitsDto> get(@RequestParam(name = "uris", required = false) List<String> uris,
                             @RequestParam(name = "unique", defaultValue = "false") Boolean unique,
                             @RequestParam(name = "start") LocalDateTime start,
                             @RequestParam(name = "end") LocalDateTime end,
                             @RequestParam(name = "app", defaultValue = "ewm-main-service") String appName) {
        return statsService.get(uris, unique, start, end, appName);
    }
}
