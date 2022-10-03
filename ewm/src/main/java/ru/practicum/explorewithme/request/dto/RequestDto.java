package ru.practicum.explorewithme.request.dto;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {
    private Long id;
    private Long requester;
    private Long event;
    private RequestState status;
    private LocalDateTime created;
}
