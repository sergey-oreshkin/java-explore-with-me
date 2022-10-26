package ru.practicum.explorewithme.statsserver.stats.db;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Stats {
    private Long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime created;
}
