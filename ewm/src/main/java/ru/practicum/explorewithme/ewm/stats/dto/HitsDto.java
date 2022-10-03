package ru.practicum.explorewithme.ewm.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HitsDto {
    private String app;
    private String uri;
    private Long hits;
}