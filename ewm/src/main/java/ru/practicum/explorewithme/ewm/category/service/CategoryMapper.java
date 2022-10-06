package ru.practicum.explorewithme.ewm.category.service;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.ewm.category.db.Category;
import ru.practicum.explorewithme.ewm.category.dto.CategoryDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    List<CategoryDto> toDto(List<Category> categories);

    Category toEntity(CategoryDto categoryDto);
}
