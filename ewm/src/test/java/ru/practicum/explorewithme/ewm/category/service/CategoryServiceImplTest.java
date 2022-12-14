package ru.practicum.explorewithme.ewm.category.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.explorewithme.ewm.category.db.Category;
import ru.practicum.explorewithme.ewm.category.db.CategoryRepository;
import ru.practicum.explorewithme.ewm.event.db.Event;
import ru.practicum.explorewithme.ewm.exception.ConflictException;
import ru.practicum.explorewithme.ewm.exception.NotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
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

    final Category category = Category.builder().id(DEFAULT_ID).name(DEFAULT_NAME).events(new HashSet<>()).build();

    @Test
    void create_shouldInvokeCategoryRepositoryAndReturnTheSame() {
        when(categoryRepository.save(category)).thenReturn(category);

        var result = categoryService.create(category);

        verify(categoryRepository, only()).save(category);

        assertNotNull(result);
        assertEquals(category, result);
    }

    @Test
    void update_shouldUpdateName() {
        Category oldCategory = Category.builder().id(DEFAULT_ID).build();

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(oldCategory));
        when(categoryRepository.save(any())).thenAnswer(returnsFirstArg());

        var result = categoryService.update(category);

        assertNotNull(result);
        assertEquals(DEFAULT_NAME, result.getName());
    }

    @Test
    void getAll_shouldReturnListOfPage() {
        Integer from = 0;
        Integer size = 10;
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(category)));

        var result = categoryService.getAll(from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(category, result.get(0));
    }

    @Test
    void getById_shouldReturnTheSame() {
        when(categoryRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(category));

        var result = categoryService.getById(DEFAULT_ID);

        assertNotNull(result);
        assertEquals(category, result);
    }

    @Test
    void getById_shouldThrow_WhenRepositoryReturnEmpty() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.getById(anyLong()));
    }

    @Test
    void delete_shouldInvokeCategoryRepositoryDelete() {
        when(categoryRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(category));
        categoryService.delete(DEFAULT_ID);

        verify(categoryRepository, times(1)).deleteById(DEFAULT_ID);
    }

    @Test
    void delete_shouldThrow_whenEventsIsNotEmpty() {
        final Event event = Event.builder().build();
        final Set<Event> events = new HashSet<>();
        events.add(event);
        final Category category = Category.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .events(events)
                .build();

        when(categoryRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(category));

        assertThrows(ConflictException.class, () -> categoryService.delete(DEFAULT_ID));
    }

}