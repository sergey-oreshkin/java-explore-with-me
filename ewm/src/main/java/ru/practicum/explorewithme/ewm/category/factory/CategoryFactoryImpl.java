package ru.practicum.explorewithme.ewm.category.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.ewm.category.db.Category;
import ru.practicum.explorewithme.ewm.category.db.CategoryRepository;
import ru.practicum.explorewithme.ewm.exception.NotFoundException;

@RequiredArgsConstructor
@Component
public class CategoryFactoryImpl implements CategoryFactory {

    private final CategoryRepository categoryRepository;

    @Override
    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found", String.format("Id=%d", id)));
    }
}
