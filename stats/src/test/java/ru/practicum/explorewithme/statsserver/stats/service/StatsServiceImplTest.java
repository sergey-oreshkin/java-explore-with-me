package ru.practicum.explorewithme.statsserver.stats.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.explorewithme.statsserver.stats.db.Stats;
import ru.practicum.explorewithme.statsserver.stats.db.StatsRepository;
import ru.practicum.explorewithme.statsserver.stats.dto.HitsDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceImplTest {
    public static final long DEFAULT_ID = 1L;
    public static final String DEFAULT_APP_NAME = "app";

    @Mock
    StatsRepository statsRepository;
    @InjectMocks
    StatsServiceImpl statsService;

    Stats stats = Stats.builder().id(DEFAULT_ID).build();
    HitsDto hitsDto = HitsDto.builder().build();

    @Test
    void create_shouldReturnTheSameStatsAndInvokeStatsRepository() {
        when(statsRepository.save(stats)).thenReturn(stats);

        var result = statsService.create(stats);

        verify(statsRepository, times(1)).save(stats);

        assertEquals(stats, result);
    }

    @Test
    void get_shouldInvokeStatsRepositoryAndReturnListOfHitsDtoWithAppName() {
        when(statsRepository.getHits(null, true, LocalDateTime.MIN, LocalDateTime.MAX, DEFAULT_APP_NAME))
                .thenReturn(List.of(hitsDto));

        var result = statsService.get(null, true, LocalDateTime.MIN, LocalDateTime.MAX, DEFAULT_APP_NAME);

        verify(statsRepository, times(1))
                .getHits(null, true, LocalDateTime.MIN, LocalDateTime.MAX, DEFAULT_APP_NAME);

        assertEquals(DEFAULT_APP_NAME, result.get(0).getApp());
    }
}