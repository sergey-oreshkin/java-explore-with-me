package ru.practicum.explorewithme.ewm.compilation.dto;

import lombok.*;
import ru.practicum.explorewithme.ewm.event.dto.EventDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompilationDto {
    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventDto> events;
}
