package ru.practicum.explorewithme.stats.db;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
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
    @CreationTimestamp
    private LocalDateTime created;
}
