package ru.practicum.explorewithme.compilation.dto;

import lombok.*;
import ru.practicum.explorewithme.event.dto.EventDto;

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
