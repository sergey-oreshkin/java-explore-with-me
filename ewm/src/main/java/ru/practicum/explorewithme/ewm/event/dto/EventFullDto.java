package ru.practicum.explorewithme.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explorewithme.ewm.category.dto.CategoryDto;
import ru.practicum.explorewithme.ewm.users.dto.ShortUserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    private Long id;
    private String title;
    private String description;
    private String adminComment;
    private String annotation;
    private CategoryDto category;
    private ShortUserDto initiator;
    private long confirmedRequests;
    private LocalDateTime createdOn;
    private LocalDateTime eventDate;
    private Boolean paid;
    private Integer participantLimit;
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private EventState state;
    private Location location;
    private long views;
}


