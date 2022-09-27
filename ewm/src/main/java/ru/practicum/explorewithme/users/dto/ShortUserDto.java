package ru.practicum.explorewithme.users.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortUserDto {
    private Long id;
    private String name;
}
