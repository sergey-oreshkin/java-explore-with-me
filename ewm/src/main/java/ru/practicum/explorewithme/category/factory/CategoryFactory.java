package ru.practicum.explorewithme.category.factory;

import ru.practicum.explorewithme.category.db.Category;

public interface CategoryFactory {
    Category getById(Long id);
}
