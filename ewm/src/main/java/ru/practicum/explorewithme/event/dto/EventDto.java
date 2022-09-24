package ru.practicum.explorewithme.event.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.explorewithme.category.dto.CategoryDto;
import ru.practicum.explorewithme.users.dto.ShortUserDto;

import java.time.LocalDateTime;

@Value
@Builder
public class EventDto {
    Long id;
    String title;
    String description;
    String annotation;
    CategoryDto category;
    ShortUserDto initiator;
    Integer confirmedRequests;
    LocalDateTime createdOn;
    LocalDateTime eventDate;
    Boolean paid;
    Integer participantLimit;
    LocalDateTime publishedOn;
    Boolean requestModeration;
    EventState state;
    Location location;
}


