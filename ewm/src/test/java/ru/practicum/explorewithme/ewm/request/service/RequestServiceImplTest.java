package ru.practicum.explorewithme.ewm.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.explorewithme.ewm.event.db.Event;
import ru.practicum.explorewithme.ewm.event.dto.EventState;
import ru.practicum.explorewithme.ewm.event.service.EventService;
import ru.practicum.explorewithme.ewm.exception.ConflictException;
import ru.practicum.explorewithme.ewm.exception.NotFoundException;
import ru.practicum.explorewithme.ewm.exception.ValidationException;
import ru.practicum.explorewithme.ewm.request.db.Request;
import ru.practicum.explorewithme.ewm.request.db.RequestRepository;
import ru.practicum.explorewithme.ewm.users.db.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    public static final Long DEFAULT_ID = 1L;
    public static final Long ANOTHER_ID = 2L;

    @Mock
    RequestRepository requestRepository;

    @Mock
    EventService eventService;

    @InjectMocks
    RequestServiceImpl requestService;

    @Test
    void create_shouldInvokeRequestRepositoryAndReturnTheSame() {

        final User requester = User.builder().id(DEFAULT_ID).build();
        final User initiator = User.builder().id(ANOTHER_ID).build();
        final Event event = Event.builder().id(DEFAULT_ID).initiator(initiator).state(EventState.PUBLISHED).requestModeration(true).build();
        final Request request = Request.builder().id(DEFAULT_ID).event(event).requester(requester).build();

        when(requestRepository.findByRequesterIdAndEventId(request.getRequester().getId(), request.getEvent().getId()))
                .thenReturn(Optional.empty());
        when(eventService.isRequestLimit(request.getEvent())).thenReturn(false);
        when(requestRepository.save(request)).thenReturn(request);

        var result = requestService.create(request);

        verify(requestRepository, times(1)).save(request);

        assertNotNull(result);
        assertEquals(request, result);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createDataForThrowException")
    void create_shouldThrow_whenBadDataReceived(String name, Request request, Class<Exception> exceptionClass,
                                                Optional<Request> returnedOpt, Boolean returnedIsLimit) {
        lenient().when(requestRepository.findByRequesterIdAndEventId(request.getRequester().getId(), request.getEvent().getId()))
                .thenReturn(returnedOpt);
        lenient().when(eventService.isRequestLimit(request.getEvent())).thenReturn(returnedIsLimit);
        lenient().when(requestRepository.save(request)).thenReturn(request);

        assertThrows(exceptionClass, () -> requestService.create(request));
    }


    @Test
    void getAllByRequester_shouldReturnTheSame() {
        final Request request = Request.builder().id(DEFAULT_ID).build();
        when(requestRepository.findAllByRequesterId(anyLong())).thenReturn(List.of(request));

        var result = requestService.getAllByRequester(anyLong());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(request, result.get(0));
    }

    @Test
    void cancel_shouldInvokeRequestRepositoryDelete() {
        final Request request = Request.builder()
                .id(DEFAULT_ID)
                .requester(User.builder().id(ANOTHER_ID).build())
                .build();

        when(requestRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(request));

        requestService.cancel(ANOTHER_ID, DEFAULT_ID);

        verify(requestRepository, times(1)).delete(request);
    }

    @Test
    void cancel_shouldThrow_whenRequestNotFound() {
        when(requestRepository.findById(DEFAULT_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.cancel(ANOTHER_ID, DEFAULT_ID));
    }

    @Test
    void cancel_shouldThrow_whenUserIsNotRequester() {
        final Request request = Request.builder()
                .id(DEFAULT_ID)
                .requester(User.builder().id(ANOTHER_ID).build())
                .build();

        when(requestRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(request));

        assertThrows(ValidationException.class, () -> requestService.cancel(DEFAULT_ID, DEFAULT_ID));
    }


    private static Stream<Arguments> createDataForThrowException() {
        return Stream.of(
                Arguments.of(
                        "Conflict exception when request exist",
                        Request.builder()
                                .id(DEFAULT_ID)
                                .event(Event.builder()
                                        .id(DEFAULT_ID)
                                        .initiator(User.builder().id(DEFAULT_ID).build())
                                        .state(EventState.PUBLISHED)
                                        .requestModeration(true)
                                        .build())
                                .requester(User.builder().id(ANOTHER_ID).build())
                                .build(),
                        ConflictException.class,
                        Optional.of(Request.builder()
                                .id(DEFAULT_ID)
                                .event(Event.builder()
                                        .id(DEFAULT_ID)
                                        .initiator(User.builder().id(DEFAULT_ID).build())
                                        .state(EventState.PUBLISHED)
                                        .requestModeration(true)
                                        .build())
                                .requester(User.builder().id(ANOTHER_ID).build())
                                .build()),
                        false
                ),
                Arguments.of(
                        "Conflict exception when requester is initiator",
                        Request.builder()
                                .id(DEFAULT_ID)
                                .event(Event.builder()
                                        .id(DEFAULT_ID)
                                        .initiator(User.builder().id(DEFAULT_ID).build())
                                        .state(EventState.PUBLISHED)
                                        .requestModeration(true)
                                        .build())
                                .requester(User.builder().id(DEFAULT_ID).build())
                                .build(),
                        ConflictException.class,
                        Optional.of(Request.builder()
                                .id(DEFAULT_ID)
                                .event(Event.builder()
                                        .id(DEFAULT_ID)
                                        .initiator(User.builder().id(DEFAULT_ID).build())
                                        .state(EventState.PUBLISHED)
                                        .requestModeration(true)
                                        .build())
                                .requester(User.builder().id(DEFAULT_ID).build())
                                .build()),
                        false
                ),
                Arguments.of(
                        "Conflict exception when event state is not published",
                        Request.builder()
                                .id(DEFAULT_ID)
                                .event(Event.builder()
                                        .id(DEFAULT_ID)
                                        .initiator(User.builder().id(DEFAULT_ID).build())
                                        .state(EventState.CANCELED)
                                        .requestModeration(true)
                                        .build())
                                .requester(User.builder().id(ANOTHER_ID).build())
                                .build(),
                        ConflictException.class,
                        Optional.of(Request.builder()
                                .id(DEFAULT_ID)
                                .event(Event.builder()
                                        .id(DEFAULT_ID)
                                        .initiator(User.builder().id(DEFAULT_ID).build())
                                        .state(EventState.CANCELED)
                                        .requestModeration(true)
                                        .build())
                                .requester(User.builder().id(ANOTHER_ID).build())
                                .build()),
                        false
                ),
                Arguments.of(
                        "Conflict exception when request limit is reached",
                        Request.builder()
                                .id(DEFAULT_ID)
                                .event(Event.builder()
                                        .id(DEFAULT_ID)
                                        .initiator(User.builder().id(DEFAULT_ID).build())
                                        .state(EventState.CANCELED)
                                        .requestModeration(true)
                                        .build())
                                .requester(User.builder().id(ANOTHER_ID).build())
                                .build(),
                        ConflictException.class,
                        Optional.of(Request.builder()
                                .id(DEFAULT_ID)
                                .event(Event.builder()
                                        .id(DEFAULT_ID)
                                        .initiator(User.builder().id(DEFAULT_ID).build())
                                        .state(EventState.CANCELED)
                                        .requestModeration(true)
                                        .build())
                                .requester(User.builder().id(ANOTHER_ID).build())
                                .build()),
                        true
                ),
                Arguments.of(
                        "Conflict exception when event state is not published",
                        Request.builder()
                                .id(DEFAULT_ID)
                                .event(Event.builder()
                                        .id(DEFAULT_ID)
                                        .initiator(User.builder().id(DEFAULT_ID).build())
                                        .state(EventState.CANCELED)
                                        .requestModeration(true)
                                        .build())
                                .requester(User.builder().id(ANOTHER_ID).build())
                                .build(),
                        ConflictException.class,
                        Optional.of(Request.builder()
                                .id(DEFAULT_ID)
                                .event(Event.builder()
                                        .id(DEFAULT_ID)
                                        .initiator(User.builder().id(DEFAULT_ID).build())
                                        .state(EventState.CANCELED)
                                        .requestModeration(true)
                                        .build())
                                .requester(User.builder().id(ANOTHER_ID).build())
                                .build()),
                        false
                )
        );
    }
}