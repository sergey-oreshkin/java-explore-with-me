package ru.practicum.explorewithme.stats.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HitsDto {
    private String app;
    private String uri;
    private Long hits;
}
