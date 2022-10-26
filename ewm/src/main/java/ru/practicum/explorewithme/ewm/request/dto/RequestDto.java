package ru.practicum.explorewithme.ewm.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {
    private Long id;
    @NotNull
    private Long requester;
    @NotNull
    private Long event;
    private RequestState status;
    private LocalDateTime created;
}
