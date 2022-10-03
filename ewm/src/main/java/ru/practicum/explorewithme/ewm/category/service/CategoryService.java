package ru.practicum.explorewithme.ewm.category.service;

import ru.practicum.explorewithme.ewm.category.db.Category;

import java.util.List;

public interface CategoryService {
    Category create(Category category);

    Category update(Category category);

    void delete(Long id);

    List<Category> getAll(Integer from, Integer size);

    Category getById(Long catId);

}
