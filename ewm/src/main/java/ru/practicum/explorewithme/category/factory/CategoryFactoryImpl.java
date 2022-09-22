package ru.practicum.explorewithme.category.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.category.db.Category;
import ru.practicum.explorewithme.category.db.CategoryRepository;
import ru.practicum.explorewithme.exception.NotFoundException;

@RequiredArgsConstructor
@Component
public class CategoryFactoryImpl implements CategoryFactory {

    private final CategoryRepository categoryRepository;

    @Override
    public Category getById(Long id){
        return categoryRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Category not found", String.format("id=%d", id)));
    }
}
