package ru.practicum.explorewithme.users.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
@Builder
@Jacksonized
public class UserDto {

    Long id;

    @NotBlank
    @Email
    @Size(max = 255)
    String email;

    @NotBlank
    @Size(max = 255)
    String name;
}
