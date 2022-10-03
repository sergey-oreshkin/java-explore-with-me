package ru.practicum.explorewithme.stats.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HitsDto {
    private String app;
    private String uri;
    private Long hits;
}