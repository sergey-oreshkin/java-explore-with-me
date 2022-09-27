package ru.practicum.explorewithme.compilation.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.explorewithme.event.dto.EventDto;

import java.util.List;

@Value
@Builder
public class CompilationDto {

    Long id;
    String title;
    Boolean pinned;
    List<EventDto> events;
}
