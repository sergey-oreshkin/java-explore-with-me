package ru.practicum.explorewithme.users.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ShortUserDto {
    Long id;
    String name;
}
