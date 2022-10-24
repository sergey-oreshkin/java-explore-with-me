package ru.practicum.explorewithme.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.ewm.category.db.Category;
import ru.practicum.explorewithme.ewm.category.db.CategoryRepository;
import ru.practicum.explorewithme.ewm.common.OffsetLimitPageable;
import ru.practicum.explorewithme.ewm.exception.ConflictException;
import ru.practicum.explorewithme.ewm.exception.NotFoundException;

import java.util.List;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category create(Category category) {
        return save(category);
    }

    @Override
    @Transactional
    public Category update(Category category) {
        Category oldCategory = getById(category.getId());
        oldCategory.setName(category.getName());
        return save(oldCategory);
    }

    @Override
    public List<Category> getAll(Integer from, Integer size) {
        Pageable pageable = OffsetLimitPageable.of(from, size);
        return categoryRepository.findAll(pageable).getContent();
    }

    @Override
    public Category getById(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category not found", format("Id=%d", catId)));
    }

    @Override
    public void delete(Long id) {
        //TODO check no events
        try {
            categoryRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("Category not found", format("Id=%d", id));
        }
    }

    private Category save(Category category){
        try {
            return categoryRepository.save(category);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("The name is already in use", format("Name=%s", category.getName()));
        }
    }
}
