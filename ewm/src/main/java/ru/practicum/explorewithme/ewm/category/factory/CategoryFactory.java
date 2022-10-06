package ru.practicum.explorewithme.ewm.category.factory;

import ru.practicum.explorewithme.ewm.category.db.Category;

public interface CategoryFactory {
    Category getById(Long id);
}
