package ru.practicum.explorewithme.compilation.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Value
@Builder
public class NewCompilationDto {
    List<Long> events;

    @NotBlank
    String title;

    @NotNull
    Boolean pinned;
}
