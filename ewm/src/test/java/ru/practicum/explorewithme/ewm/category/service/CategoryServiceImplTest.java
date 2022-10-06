package ru.practicum.explorewithme.ewm.category.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.explorewithme.ewm.category.db.Category;
import ru.practicum.explorewithme.ewm.category.db.CategoryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    public static final long DEFAULT_ID = 1L;
    public static final String DEFAULT_NAME = "the name";

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryServiceImpl categoryService;
    Category category = Category.builder().id(DEFAULT_ID).name(DEFAULT_NAME).build();

    @Test
    void create_shouldInvokeCategoryRepositoryAndReturnTheSame() {
        when(categoryRepository.save(category)).thenReturn(category);

        var result = categoryService.create(category);

        verify(categoryRepository, only()).save(category);

        assertEquals(category, result);
    }

    @Test
    void update_shouldUpdateName() {
        Category oldCategory = Category.builder().id(DEFAULT_ID).build();

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(oldCategory));
        when(categoryRepository.save(any())).thenAnswer(returnsFirstArg());

        var result = categoryService.update(category);

        assertEquals(DEFAULT_NAME, result.getName());
    }

    @Test
    void getAll() {
    }

    @Test
    void getById() {
    }

    @Test
    void delete() {
    }
}