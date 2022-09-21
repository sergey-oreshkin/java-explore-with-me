package ru.practicum.explorewithme.category.service;

import ru.practicum.explorewithme.category.db.Category;

public interface CategoryService {
    Category create(Category category);

    Category update(Category category);

    void delete(Long id);
}
