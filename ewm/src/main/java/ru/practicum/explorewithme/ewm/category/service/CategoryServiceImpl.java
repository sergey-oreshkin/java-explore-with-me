package ru.practicum.explorewithme.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.ewm.category.db.Category;
import ru.practicum.explorewithme.ewm.category.db.CategoryRepository;
import ru.practicum.explorewithme.ewm.common.OffsetLimitPageable;
import ru.practicum.explorewithme.ewm.exception.NotFoundException;

import java.util.List;

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
    public List<Category> getAll(Integer from, Integer size) {
        Pageable pageable = OffsetLimitPageable.of(from, size);
        return categoryRepository.findAll(pageable).getContent();
    }

    @Override
    public Category getById(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category not found", String.format("id=%d", catId)));
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
