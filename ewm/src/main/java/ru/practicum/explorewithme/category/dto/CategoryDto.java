package ru.practicum.explorewithme.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {

    private Long id;

    @NotBlank
    @Size(max = 255)
    private String name;
}
