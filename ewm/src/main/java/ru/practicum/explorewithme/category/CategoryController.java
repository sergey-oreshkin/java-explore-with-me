package ru.practicum.explorewithme.category;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.category.db.Category;
import ru.practicum.explorewithme.category.dto.CategoryDto;
import ru.practicum.explorewithme.category.service.CategoryMapper;
import ru.practicum.explorewithme.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    private final CategoryMapper mapper;

    @PostMapping
    public CategoryDto create(@Valid @RequestBody CategoryDto categoryDto) {
        Category category = mapper.toEntity(categoryDto);
        return mapper.toDto(categoryService.create(category));
    }

    @PatchMapping
    public CategoryDto update(@Valid @RequestBody CategoryDto categoryDto) {
        Category category = mapper.toEntity(categoryDto);
        return mapper.toDto(categoryService.update(category));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable @NotNull Long id) {
        categoryService.delete(id);
    }
}
