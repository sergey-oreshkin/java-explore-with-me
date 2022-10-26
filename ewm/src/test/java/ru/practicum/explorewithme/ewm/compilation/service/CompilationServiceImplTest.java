package ru.practicum.explorewithme.ewm.compilation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.explorewithme.ewm.common.OffsetLimitPageable;
import ru.practicum.explorewithme.ewm.compilation.db.Compilation;
import ru.practicum.explorewithme.ewm.compilation.db.CompilationRepository;
import ru.practicum.explorewithme.ewm.event.db.Event;
import ru.practicum.explorewithme.ewm.event.factory.EventFactory;
import ru.practicum.explorewithme.ewm.exception.NotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompilationServiceImplTest {

    public static final Long DEFAULT_ID = 1L;

    @Mock
    CompilationRepository compilationRepository;

    @Mock
    EventFactory eventFactory;

    @InjectMocks
    CompilationServiceImpl compilationService;

    @Test
    void create_shouldInvokeRepositoryAndReturnTheSame() {
        final Compilation compilation = Compilation.builder().id(DEFAULT_ID).build();
        when(compilationRepository.save(compilation)).thenReturn(compilation);

        var result = compilationService.create(compilation);

        verify(compilationRepository, times(1)).save(compilation);

        assertNotNull(result);
        assertEquals(compilation, result);
    }

    @Test
    void delete_shouldInvokeCompilationRepositoryDelete() {
        compilationService.delete(anyLong());

        verify(compilationRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void pin_shouldInvokeRepository() {
        final Compilation compilation = Compilation.builder().id(DEFAULT_ID).build();
        when(compilationRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(compilation));

        compilationService.pin(DEFAULT_ID, true);

        verify(compilationRepository, times(1)).save(compilation);
    }

    @Test
    void addEvent_shouldInvokeRepository() {
        final Compilation compilation = Compilation.builder().id(DEFAULT_ID).events(new HashSet<>()).build();
        when(compilationRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(compilation));

        compilationService.addEvent(DEFAULT_ID, DEFAULT_ID);

        verify(compilationRepository, times(1)).save(compilation);
    }

    @Test
    void deleteEvent_shouldInvokeRepository() {
        final Event event = Event.builder().id(DEFAULT_ID).build();
        final Set<Event> events = new HashSet<>();
        events.add(event);
        final Compilation compilation = Compilation.builder().id(DEFAULT_ID).events(events).build();
        when(compilationRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(compilation));
        when(eventFactory.getById(DEFAULT_ID)).thenReturn(event);

        compilationService.deleteEvent(DEFAULT_ID, DEFAULT_ID);

        verify(compilationRepository, times(1)).save(compilation);
    }

    @Test
    void getAll_shouldInvokeFindAllByPinnedAndReturnTheSame_whenPinnedIsNotNull() {
        final Compilation compilation = Compilation.builder().id(DEFAULT_ID).build();
        when(compilationRepository.findAllByPinned(eq(true), any(OffsetLimitPageable.class))).thenReturn(List.of(compilation));

        var result = compilationService.getAll(0, 10, true);

        verify(compilationRepository, times(1)).findAllByPinned(eq(true), any(OffsetLimitPageable.class));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(compilation, result.get(0));
    }

    @Test
    void getAll_shouldInvokeFindAllAndReturnTheSame_whenPinnedIsNull() {
        final Compilation compilation = Compilation.builder().id(DEFAULT_ID).build();
        when(compilationRepository.findAll(any(OffsetLimitPageable.class))).thenReturn(new PageImpl<>(List.of(compilation)));

        var result = compilationService.getAll(0, 10, null);

        verify(compilationRepository, times(1)).findAll(any(OffsetLimitPageable.class));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(compilation, result.get(0));
    }

    @Test
    void getById_shouldReturnTheSame() {
        final Compilation compilation = Compilation.builder().id(DEFAULT_ID).build();
        when(compilationRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(compilation));

        var result = compilationService.getById(DEFAULT_ID);

        assertNotNull(result);
        assertEquals(compilation, result);
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(compilationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> compilationService.getById(DEFAULT_ID));
    }
}