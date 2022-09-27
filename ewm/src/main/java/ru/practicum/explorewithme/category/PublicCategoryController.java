package ru.practicum.explorewithme.category;


import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.category.dto.CategoryDto;
import ru.practicum.explorewithme.category.service.CategoryMapper;
import ru.practicum.explorewithme.category.service.CategoryService;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
public class PublicCategoryController {

    private final CategoryService categoryService;

    private final CategoryMapper mapper;

    @GetMapping
    public List<CategoryDto> getAll(@RequestParam(name = "from", defaultValue = "0") Integer from,
                                    @RequestParam(name = "size", defaultValue = "10") Integer size){
        return mapper.toDto(categoryService.getAll(from, size));
    }

    @GetMapping("{catId}")
    public CategoryDto get(@PathVariable @NotNull Long catId){
        return mapper.toDto(categoryService.getById(catId));
    }

}
