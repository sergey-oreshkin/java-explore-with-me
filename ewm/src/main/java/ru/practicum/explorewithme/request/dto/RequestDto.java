package ru.practicum.explorewithme.request.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;


@Value
@Builder
public class RequestDto {
    Long id;
    Long requester;
    Long event;
    RequestState state;
    LocalDateTime created;
}
