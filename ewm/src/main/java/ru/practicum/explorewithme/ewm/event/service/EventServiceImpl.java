package ru.practicum.explorewithme.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.ewm.common.OffsetLimitPageable;
import ru.practicum.explorewithme.ewm.common.SortType;
import ru.practicum.explorewithme.ewm.event.db.Event;
import ru.practicum.explorewithme.ewm.event.db.EventRepositoryFacade;
import ru.practicum.explorewithme.ewm.event.dto.EventDto;
import ru.practicum.explorewithme.ewm.event.dto.EventState;
import ru.practicum.explorewithme.ewm.exception.ConflictException;
import ru.practicum.explorewithme.ewm.exception.NotFoundException;
import ru.practicum.explorewithme.ewm.exception.ValidationException;
import ru.practicum.explorewithme.ewm.request.db.Request;
import ru.practicum.explorewithme.ewm.request.db.RequestRepository;
import ru.practicum.explorewithme.ewm.request.dto.RequestState;
import ru.practicum.explorewithme.ewm.users.db.User;
import ru.practicum.explorewithme.ewm.users.factory.UserFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepositoryFacade eventRepository;

    private final EventMapper mapper;

    private final UserFactory userFactory;

    private final RequestRepository requestRepository;

    @Value("${app.minMinutesToEvent}")
    private int minMinutesToEvent;

    @Override
    public Event create(Event event) {
        validateEventDateOrThrow(event.getEventDate());
        event.setState(EventState.PENDING);
        return eventRepository.save(event);
    }

    @Override
    public Event update(Event event) {
        if (event.getId() == null) {
            throw new ValidationException("Event id must not be null", String.valueOf(event.getId()));
        }
        Event oldEvent = eventRepository.findById(event.getId())
                .orElseThrow(() -> new NotFoundException("Event not found", String.format("id=%d", event.getId())));
        if (oldEvent.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Published event can not be changed", EventState.PUBLISHED.toString());
        }
        if (event.getEventDate() != null) {
            validateEventDateOrThrow(event.getEventDate());
        }
        mapper.updateEvent(event, oldEvent); // patch oldEvent
        oldEvent.setState(EventState.PENDING);
        return eventRepository.save(oldEvent);
    }

    @Override
    public List<Event> getAll(Long userId, Integer from, Integer size) {
        Pageable pageable = OffsetLimitPageable.of(from, size);
        return eventRepository.findAllByInitiatorId(userId, pageable);
    }

    @Override
    public Event get(Long userId, Long eventId) {
        User user = userFactory.getById(userId);
        return eventRepository.findByIdAndInitiator(eventId, user)
                .orElseThrow(() -> new NotFoundException("No Event found", String.format("EventId=%d, InitiatorId=%d", eventId, userId)));
    }

    @Override
    public Event cancel(Long userId, Long eventId) {
        Event event = get(userId, eventId);
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Published event can not be cancelled", EventState.PUBLISHED.toString());
        }
        event.setState(EventState.CANCELED);
        return eventRepository.save(event);
    }

    @Override
    public List<Event> getAll(List<Long> users, List<EventState> states, List<Long> categories,
                              LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        return eventRepository.findAllByParameters(users, states, categories, null, rangeStart, rangeEnd, null, null, from, size);
    }

    @Override
    public Event setEventState(Long eventId, EventState state) {
        Event event = getEventOrThrow(eventId);
        event.setState(state);
        event.setPublished(LocalDateTime.now());
        return eventRepository.save(event);
    }

    @Override
    public List<Request> getRequests(Long userId, Long eventId) {
        Event event = getEventOrThrow(eventId);
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ValidationException("Only initiator can get requests", String.format("initiatorId=%d", userId));
        }
        return new ArrayList<>(event.getRequests());
    }

    @Override
    public Request confirm(Long userId, Long eventId, Long reqId) {
        Event event = getEventOrThrow(eventId);
        if (!event.getRequestModeration()) {
            throw new ConflictException("The event does not require confirmation", "request moderation is false");
        }
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ValidationException("Only initiator can confirm the request", String.format("initiatorId=%d", userId));
        }
        if (isRequestLimit(event)) {
            throw new ConflictException("Request limit has been reached", String.format("Request limit=%d", event.getParticipantLimit()));
        }
        Request request = requestRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundException("Request not found", String.format("id=%d", reqId)));
        if (!event.getRequests().contains(request)) {
            throw new ValidationException("Wrong event for the request", String.format("EventId=%d, RequestId=%d", eventId, reqId));
        }
        request.setState(RequestState.CONFIRMED);
        requestRepository.save(request);

        List<Request> requests = requestRepository.findAllByEventId(eventId);

        long confirmedRequestsCount = requests.stream().filter(r -> r.getState() == RequestState.CONFIRMED).count();
        if (confirmedRequestsCount >= event.getParticipantLimit()) {
            requests.stream().filter(r -> r.getState() != RequestState.CONFIRMED).forEach(requestRepository::delete);
        }
        return request;
    }

    @Override
    public Request reject(Long userId, Long eventId, Long reqId) {
        Event event = getEventOrThrow(eventId);
        if (!event.getRequestModeration()) {
            throw new ConflictException("The event does not require confirmation", "request moderation is false");
        }
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ValidationException("Only initiator can confirm the request", String.format("initiatorId=%d", userId));
        }
        Request request = requestRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundException("Request not found", String.format("id=%d", reqId)));
        if (!event.getRequests().contains(request)) {
            throw new ValidationException("Wrong event for the request", String.format("EventId=%d, RequestId=%d", eventId, reqId));
        }
        requestRepository.delete(request);
        request.setState(RequestState.REJECTED);
        return request;
    }

    @Override
    public Event getPublishedById(Long eventId) {
        return eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event not found or not published yet", String.format("id=%d", eventId)));
    }

    @Override
    public List<EventDto> getAllPublished(String text, List<Long> categories, Boolean paid, Boolean onlyAvailable, SortType sortType,
                                          LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        List<Event> events = eventRepository.findAllByParameters(
                null, List.of(EventState.PUBLISHED), categories, paid, rangeStart, rangeEnd, text, onlyAvailable, from, size
        );
        if (Objects.nonNull(onlyAvailable) && onlyAvailable) {
            events = events.stream().filter(e -> !isRequestLimit(e)).collect(Collectors.toList());
        }
        return getSortedEvents(events, sortType);
    }

    @Override
    public boolean isRequestLimit(Event event) {
        if (event.getParticipantLimit() == null || event.getParticipantLimit() == 0) {
            return false;
        }
        long confirmedRequestsCount = event.getRequests().stream().filter(r -> r.getState() == RequestState.CONFIRMED).count();
        return confirmedRequestsCount >= event.getParticipantLimit();
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found", String.format("id=%d", eventId)));
    }

    private void validateEventDateOrThrow(LocalDateTime eventDate) {
        LocalDateTime minTime = LocalDateTime.now().plusMinutes(minMinutesToEvent);
        if (minTime.isAfter(eventDate)) {
            throw new ValidationException(String.format("Must be minimum %d minutes before event start", minMinutesToEvent),
                    eventDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }

    private List<EventDto> getSortedEvents(List<Event> events, SortType sortType) {
        List<EventDto> eventDtos = mapper.toDto(events);
        if (Objects.nonNull(sortType)) {
            switch (sortType) {
                case VIEWS:
                    return eventDtos.stream().sorted(Comparator.comparing(EventDto::getViews)).collect(Collectors.toList());
                case EVENT_DATE:
                    return eventDtos.stream().sorted(Comparator.comparing(EventDto::getEventDate)).collect(Collectors.toList());
            }
        }
        return eventDtos;
    }
}
