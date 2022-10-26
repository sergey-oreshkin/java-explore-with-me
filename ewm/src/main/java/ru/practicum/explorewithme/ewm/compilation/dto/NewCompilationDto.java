package ru.practicum.explorewithme.ewm.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewCompilationDto {

    private List<Long> events;

    @NotNull
    @Size(max = 1024)
    private String title;

    @NotNull
    private Boolean pinned;
}
