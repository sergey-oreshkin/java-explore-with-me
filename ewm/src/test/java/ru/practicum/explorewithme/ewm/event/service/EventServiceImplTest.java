package ru.practicum.explorewithme.ewm.event.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.explorewithme.ewm.common.OffsetLimitPageable;
import ru.practicum.explorewithme.ewm.event.db.Event;
import ru.practicum.explorewithme.ewm.event.db.EventRepositoryFacade;
import ru.practicum.explorewithme.ewm.event.dto.EventState;
import ru.practicum.explorewithme.ewm.exception.ConflictException;
import ru.practicum.explorewithme.ewm.exception.NotFoundException;
import ru.practicum.explorewithme.ewm.exception.ValidationException;
import ru.practicum.explorewithme.ewm.users.db.User;
import ru.practicum.explorewithme.ewm.users.factory.UserFactory;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    public static final Long DEFAULT_ID = 1L;

    @Mock
    EventRepositoryFacade eventRepository;

    @Spy
    EventMapper mapper = Mappers.getMapper(EventMapper.class);

    @Mock
    UserFactory userFactory;

    @InjectMocks
    EventServiceImpl eventService;


    @Test
    void create_shouldInvokeRepositoryAndReturnTheSame() {
        Event event = Event.builder().eventDate(LocalDateTime.MAX).build();
        when(eventRepository.save(any())).thenAnswer(returnsFirstArg());

        var result = eventService.create(event);

        verify(eventRepository, times(1)).save(any());

        assertNotNull(result);
        assertEquals(EventState.PENDING, result.getState());
    }

    @Test
    void create_shouldThrow_whenDateIsIllegal() {
        Event event = Event.builder().eventDate(LocalDateTime.MIN).build();

        assertThrows(ValidationException.class, () -> eventService.create(event));
    }

    @Test
    void update_shouldInvokeRepositoryAndReturnUpdated() {
        Event newEvent = Event.builder().id(DEFAULT_ID).eventDate(LocalDateTime.MAX).title("new title").build();
        Event oldEvent = Event.builder().id(DEFAULT_ID).eventDate(LocalDateTime.MAX).title("old title").build();

        when(eventRepository.findById(newEvent.getId())).thenReturn(Optional.of(oldEvent));
        when(eventRepository.save(any())).thenAnswer(returnsFirstArg());

        var result = eventService.update(newEvent);

        verify(eventRepository, times(1)).save(any());

        assertNotNull(result);
        assertEquals(newEvent.getTitle(), result.getTitle());
        assertEquals(EventState.PENDING, result.getState());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("dataForUpdateTest")
    void update_shouldThrow(String name, Event event, Optional<Event> optional, Class<Exception> exceptionClass) {
        lenient().when(eventRepository.findById(anyLong())).thenReturn(optional);

        assertThrows(exceptionClass, () -> eventService.update(event));
    }

    private static Stream<Arguments> dataForUpdateTest() {
        Event defaultEvent = Event.builder().id(DEFAULT_ID).eventDate(LocalDateTime.MIN).build();
        return Stream.of(
                Arguments.of(
                        "ValidationException when date is illegal",
                        Event.builder().id(DEFAULT_ID).eventDate(LocalDateTime.MIN).build(),
                        Optional.of(defaultEvent),
                        ValidationException.class
                ),
                Arguments.of(
                        "NotFoundException when id not found",
                        Event.builder().id(DEFAULT_ID).eventDate(LocalDateTime.MIN).build(),
                        Optional.empty(),
                        NotFoundException.class
                ),
                Arguments.of(
                        "ConflictException when state is PUBLISHED",
                        Event.builder().id(DEFAULT_ID).eventDate(LocalDateTime.MAX).state(EventState.PUBLISHED).build(),
                        Optional.of(Event.builder().id(DEFAULT_ID).eventDate(LocalDateTime.MAX).state(EventState.PUBLISHED).build()),
                        ConflictException.class
                ),
                Arguments.of(
                        "ValidationException when id is null",
                        Event.builder().build(),
                        Optional.of(defaultEvent),
                        ValidationException.class
                )
        );
    }

    @Test
    void getAll_shouldReturnTheSame() {
        Event event = Event.builder().id(DEFAULT_ID).eventDate(LocalDateTime.MIN).build();
        when(eventRepository.findAllByInitiatorId(anyLong(), any(OffsetLimitPageable.class))).thenReturn(List.of(event));

        var result = eventService.getAll(DEFAULT_ID, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(event, result.get(0));
    }

    @Test
    void get_shouldReturnTheSame() {
        User user = User.builder().id(DEFAULT_ID).build();
        Event event = Event.builder().id(DEFAULT_ID).build();
        when(eventRepository.findByIdAndInitiator(DEFAULT_ID, user)).thenReturn(Optional.of(event));

        var result = eventService.get(DEFAULT_ID, DEFAULT_ID);

        assertNotNull(result);
        assertEquals(event, result);
    }

    @Test
    void cancel_shouldReturnTheSame() {
        User user = User.builder().id(DEFAULT_ID).build();
        Event event = Event.builder().id(DEFAULT_ID).build();
        when(userFactory.getById(DEFAULT_ID)).thenReturn(user);
        when(eventRepository.findByIdAndInitiator(DEFAULT_ID, user)).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenAnswer(returnsFirstArg());

        var result = eventService.cancel(DEFAULT_ID, DEFAULT_ID);

        verify(eventRepository, times(1)).save(any());

        assertNotNull(result);
        assertEquals(EventState.CANCELED, result.getState());
    }

    @Test
    void cancel_shouldThrow_whenStateIsPublished() {
        User user = User.builder().id(DEFAULT_ID).build();
        Event event = Event.builder().id(DEFAULT_ID).state(EventState.PUBLISHED).build();
        when(userFactory.getById(DEFAULT_ID)).thenReturn(user);
        when(eventRepository.findByIdAndInitiator(DEFAULT_ID, user)).thenReturn(Optional.of(event));

        assertThrows(ConflictException.class, () -> eventService.cancel(DEFAULT_ID, DEFAULT_ID));
    }

    @Test
    void parametrizedGetAll_shouldReturnTheSame() {
        Event event = Event.builder().id(DEFAULT_ID).eventDate(LocalDateTime.MIN).build();
        when(eventRepository.findAllByParameters(anyList(), anyList(), anyList(), eq(null), any(), any(), eq(null), eq(null), anyInt(), anyInt()))
                .thenReturn(List.of(event, event));

        var result = eventService.getAll(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                LocalDateTime.MIN, LocalDateTime.MAX, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(event, result.get(0));
    }

    @Test
    void setEventState() {
    }

    @Test
    void getRequests() {
    }

    @Test
    void confirm() {
    }

    @Test
    void reject() {
    }

    @Test
    void getPublishedById() {
    }

    @Test
    void getAllPublished() {
    }

    @Test
    void isRequestLimit() {
    }
}