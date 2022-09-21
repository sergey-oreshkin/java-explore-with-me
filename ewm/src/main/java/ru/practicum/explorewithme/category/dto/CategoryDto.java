package ru.practicum.explorewithme.category.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
@Builder
public class CategoryDto {

    Long id;

    @NotBlank
    @Size(max = 255)
    String name;
}
