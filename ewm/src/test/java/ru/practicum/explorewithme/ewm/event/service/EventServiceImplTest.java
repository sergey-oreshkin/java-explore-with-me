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
import ru.practicum.explorewithme.ewm.request.db.Request;
import ru.practicum.explorewithme.ewm.request.dto.RequestState;
import ru.practicum.explorewithme.ewm.users.db.User;
import ru.practicum.explorewithme.ewm.users.factory.UserFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    public static final Long DEFAULT_ID = 1L;

    public static final Long ANOTHER_ID = 2L;

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
        Event event = Event.builder().id(DEFAULT_ID).initiator(user).build();
        when(eventRepository.findByIdAndInitiator(DEFAULT_ID, user)).thenReturn(Optional.of(event));
        when(userFactory.getById(DEFAULT_ID)).thenReturn(user);

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
    void setEventState_shouldInvokeRepositoryAndReturnWithNewState() {
        Event event = Event.builder().id(DEFAULT_ID).eventDate(LocalDateTime.MAX).state(EventState.PENDING).build();
        when(eventRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenAnswer(returnsFirstArg());

        var result = eventService.setEventState(DEFAULT_ID, EventState.PUBLISHED, null);

        verify(eventRepository, times(1)).save(any());

        assertNotNull(result);
        assertEquals(EventState.PUBLISHED, result.getState());
        assertNotNull(result.getPublished());
    }

    @Test
    void getRequests_shouldReturnListOfEventRequests() {
        Request request = Request.builder().id(DEFAULT_ID).build();
        User user = User.builder().id(DEFAULT_ID).build();
        Event event = Event.builder().id(DEFAULT_ID).requests(Set.of(request)).initiator(user).build();
        when(eventRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(event));

        var result = eventService.getRequests(DEFAULT_ID, DEFAULT_ID);

        assertNotNull(result);
        assertEquals(List.of(request), result);
    }

    @Test
    void getRequests_shouldThrow_whenUserIdIsNotInitiator() {
        User user = User.builder().id(DEFAULT_ID).build();
        Event event = Event.builder().id(DEFAULT_ID).initiator(user).build();
        when(eventRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(event));

        assertThrows(ValidationException.class, () -> eventService.getRequests(ANOTHER_ID, DEFAULT_ID));
    }

    @Test
    void confirm_shouldSetRequestToConfirmedAndReturnTheSame() {
        Request request = Request.builder().id(DEFAULT_ID).build();
        User user = User.builder().id(DEFAULT_ID).build();
        Event event = Event.builder().id(DEFAULT_ID).requests(Set.of(request)).requestModeration(true).initiator(user).build();
        when(eventRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenAnswer(returnsFirstArg());

        var result = eventService.confirm(DEFAULT_ID, DEFAULT_ID, DEFAULT_ID);

        verify(eventRepository, times(1)).save(any());

        assertNotNull(result);
        assertEquals(RequestState.CONFIRMED, result.getState());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("dataForConfirmTest")
    void confirm_shouldThrow_whenSomethingWrong(String name, Event event, Long eventId, Long userId, Long reqId, Class<Exception> exceptionClass) {
        when(eventRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(event));

        assertThrows(exceptionClass, () -> eventService.confirm(userId, eventId, reqId));
    }

    private static Stream<Arguments> dataForConfirmTest() {
        return Stream.of(
                Arguments.of(
                        "ConflictException when requestModeration is false",
                        Event.builder().id(DEFAULT_ID).requestModeration(false).build(),
                        DEFAULT_ID, DEFAULT_ID, DEFAULT_ID,
                        ConflictException.class
                ),
                Arguments.of(
                        "ValidationException when userId is not initiator",
                        Event.builder().id(DEFAULT_ID).requestModeration(true).initiator(User.builder().id(DEFAULT_ID).build()).build(),
                        DEFAULT_ID, ANOTHER_ID, DEFAULT_ID,
                        ValidationException.class
                ),
                Arguments.of(
                        "ConflictException when limit is reached",
                        Event.builder().id(DEFAULT_ID)
                                .requestModeration(true)
                                .initiator(User.builder().id(DEFAULT_ID).build())
                                .requests(new HashSet<>(List.of(Request.builder().id(DEFAULT_ID).state(RequestState.CONFIRMED).build())))
                                .participantLimit(1)
                                .build(),
                        DEFAULT_ID, DEFAULT_ID, DEFAULT_ID,
                        ConflictException.class
                ),
                Arguments.of(
                        "ValidationException when request id not found",
                        Event.builder().id(DEFAULT_ID)
                                .requestModeration(true)
                                .initiator(User.builder().id(DEFAULT_ID).build())
                                .requests(new HashSet<>(List.of(Request.builder().id(DEFAULT_ID).state(RequestState.CONFIRMED).build())))
                                .participantLimit(10)
                                .build(),
                        DEFAULT_ID, DEFAULT_ID, ANOTHER_ID,
                        ValidationException.class
                )
        );
    }

    @Test
    void reject_shouldSetRequestToRejectedAndReturnTheSame() {
        Request request = Request.builder().id(DEFAULT_ID).build();
        User user = User.builder().id(DEFAULT_ID).build();
        Event event = Event.builder().id(DEFAULT_ID).requests(new HashSet<>(List.of(request))).requestModeration(true).initiator(user).build();
        when(eventRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenAnswer(returnsFirstArg());

        var result = eventService.reject(DEFAULT_ID, DEFAULT_ID, DEFAULT_ID);

        verify(eventRepository, times(1)).save(any());

        assertNotNull(result);
        assertEquals(RequestState.REJECTED, result.getState());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("dataForRejectTest")
    void reject_shouldThrow_whenSomethingWrong(String name, Event event, Long eventId, Long userId, Long reqId, Class<Exception> exceptionClass) {
        when(eventRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(event));

        assertThrows(exceptionClass, () -> eventService.reject(userId, eventId, reqId));
    }

    private static Stream<Arguments> dataForRejectTest() {
        return Stream.of(
                Arguments.of(
                        "ConflictException when requestModeration is false",
                        Event.builder().id(DEFAULT_ID).requestModeration(false).build(),
                        DEFAULT_ID, DEFAULT_ID, DEFAULT_ID,
                        ConflictException.class
                ),
                Arguments.of(
                        "ValidationException when userId is not initiator",
                        Event.builder().id(DEFAULT_ID).requestModeration(true).initiator(User.builder().id(DEFAULT_ID).build()).build(),
                        DEFAULT_ID, ANOTHER_ID, DEFAULT_ID,
                        ValidationException.class
                ),
                Arguments.of(
                        "ValidationException when request id not found",
                        Event.builder().id(DEFAULT_ID)
                                .requestModeration(true)
                                .initiator(User.builder().id(DEFAULT_ID).build())
                                .requests(new HashSet<>(List.of(Request.builder().id(DEFAULT_ID).state(RequestState.CONFIRMED).build())))
                                .participantLimit(10)
                                .build(),
                        DEFAULT_ID, DEFAULT_ID, ANOTHER_ID,
                        ValidationException.class
                )
        );
    }

    @Test
    void getPublishedById_shouldReturnPublishedEventById() {
        Event event = Event.builder().id(DEFAULT_ID).build();
        when(eventRepository.findByIdAndState(DEFAULT_ID, EventState.PUBLISHED)).thenReturn(Optional.of(event));

        var result = eventService.getPublishedById(DEFAULT_ID);

        assertNotNull(result);
        assertEquals(event, result);
    }

    @Test
    void getPublishedById_shouldThrow_whenEventNotFound() {
        when(eventRepository.findByIdAndState(anyLong(), any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventService.getPublishedById(DEFAULT_ID));
    }

    @Test
    void getAllPublished_shouldReturnAllByParams() {
        Event event = Event.builder().id(DEFAULT_ID).build();
        when(eventRepository.findAllByParameters(eq(null), eq(List.of(EventState.PUBLISHED)), anyList(), anyBoolean(), any(), any(), anyString(), anyBoolean(), anyInt(), anyInt()))
                .thenReturn(List.of(event));

        var result = eventService.getAllPublished("", Collections.emptyList(), true, false,
                LocalDateTime.MIN, LocalDateTime.MAX, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(event, result.get(0));
    }

    @Test
    void isRequestLimit_shouldReturnFalse_whenLimitIsZero() {
        Event event = Event.builder().participantLimit(0).build();

        assertFalse(eventService.isRequestLimit(event));
    }

    @Test
    void isRequestLimit_shouldReturnTrue_whenConfirmedRequestCountEqualOrMoreThenParticipantLimit() {
        Event event = Event.builder().participantLimit(1)
                .requests(new HashSet<>(List.of(Request.builder().id(DEFAULT_ID).state(RequestState.CONFIRMED).build())))
                .build();

        assertTrue(eventService.isRequestLimit(event));
    }
}