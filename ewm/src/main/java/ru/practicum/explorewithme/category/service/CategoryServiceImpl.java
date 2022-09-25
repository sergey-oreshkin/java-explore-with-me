package ru.practicum.explorewithme.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.category.db.Category;
import ru.practicum.explorewithme.category.db.CategoryRepository;
import ru.practicum.explorewithme.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category create(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category update(Category category) {
        Category oldCategory = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new NotFoundException("Category not found", String.format("id=%d", category.getId())));
        oldCategory.setName(category.getName());
        return categoryRepository.save(oldCategory);
    }


    @Override
    public void delete(Long id) {
        try {
            categoryRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("Category not found", String.format("id=%d", id));
        }
    }
}
