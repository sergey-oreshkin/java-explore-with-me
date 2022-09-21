package ru.practicum.explorewithme.category.service;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.category.db.Category;
import ru.practicum.explorewithme.category.dto.CategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);
    Category toEntity(CategoryDto categoryDto);
}
