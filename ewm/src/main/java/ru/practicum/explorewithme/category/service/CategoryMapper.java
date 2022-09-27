package ru.practicum.explorewithme.category.service;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.category.db.Category;
import ru.practicum.explorewithme.category.dto.CategoryDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    List<CategoryDto> toDto(List<Category> categories);

    Category toEntity(CategoryDto categoryDto);
}
