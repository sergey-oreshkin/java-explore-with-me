package ru.practicum.explorewithme.event.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Value
@Builder
@Jacksonized
public class NewEventDto {

    Long eventId;

    @NotBlank
    @Size(max = 1024)
    String title;

    @NotBlank
    String description;

    @NotBlank
    String annotation;

    @NotNull
    Integer category;

    @NotNull
    LocalDateTime eventDate;

    @NotNull
    Boolean paid;

    @NotNull
    @Positive
    Integer participantLimit;

    @NotNull
    Boolean requestModeration;

    Location location;
}
