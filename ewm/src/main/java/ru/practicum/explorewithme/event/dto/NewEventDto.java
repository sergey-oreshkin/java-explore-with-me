package ru.practicum.explorewithme.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {

    private Long eventId;

    @NotBlank
    @Size(max = 1024)
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String annotation;

    @NotNull
    private Integer category;

    @NotNull
    private LocalDateTime eventDate;

    @NotNull
    private Boolean paid;

    @NotNull
    @Min(value = 0L)
    private Integer participantLimit;

    @NotNull
    private Boolean requestModeration;

    private Location location;
}
