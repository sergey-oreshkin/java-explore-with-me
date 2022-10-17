package ru.practicum.explorewithme.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepositoryFacade eventRepository;

    private final EventMapper mapper;

    private final UserFactory userFactory;


    @Value("${app.minMinutesToEvent}")
    private int minMinutesToEvent;

    @Override
    @Transactional
    public Event create(Event event) {
        validateEventDateOrThrow(event.getEventDate());
        event.setState(EventState.PENDING);
        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public Event update(Event event) {
        if (event.getId() == null) {
            throw new ValidationException("Event id must not be null", "EventId=null");
        }
        Event oldEvent = eventRepository.findById(event.getId())
                .orElseThrow(() -> new NotFoundException("Event not found", format("Id=%d", event.getId())));
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
                .orElseThrow(() -> new NotFoundException("No Event found", format("EventId=%d, InitiatorId=%d", eventId, userId)));
    }

    @Override
    @Transactional
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
        return eventRepository.findAllByParameters(users, states, categories, null, rangeStart, rangeEnd,
                null, null, from, size);
    }

    @Override
    @Transactional
    public Event setEventState(Long eventId, EventState state, @Nullable String comment) {
        Event event = getEventOrThrow(eventId);
        event.setState(state);
        if (state == EventState.PUBLISHED) {
            event.setPublished(LocalDateTime.now());
        }
        if (Objects.nonNull(comment) && state == EventState.CANCELED) {
            event.setAdminComment(comment);
        }
        return eventRepository.save(event);
    }

    @Override
    public List<Request> getRequests(Long userId, Long eventId) {
        Event event = getEventOrThrow(eventId);
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ValidationException("Only initiator can get requests", format("InitiatorId=%d", userId));
        }
        return new ArrayList<>(event.getRequests());
    }

    @Override
    @Transactional
    public Request confirm(Long userId, Long eventId, Long reqId) {
        Event event = getEventOrThrow(eventId);
        if (!event.getRequestModeration()) {
            throw new ConflictException("The event does not require confirmation", "request moderation is false");
        }
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ValidationException("Only initiator can confirm the request", format("InitiatorId=%d", userId));
        }
        if (isRequestLimit(event)) {
            throw new ConflictException("Request limit has been reached", format("Request limit=%d", event.getParticipantLimit()));
        }
        Request request = event.getRequests().stream().filter(r -> Objects.equals(r.getId(), reqId)).findFirst()
                .orElseThrow(() -> new ValidationException("Wrong event for the request", format("EventId=%d, RequestId=%d", eventId, reqId)));
        request.setState(RequestState.CONFIRMED);
        if (isRequestLimit(event)) {
            event.getRequests().removeIf(r -> r.getState() != RequestState.CONFIRMED);
        }
        eventRepository.save(event);
        return request;
    }

    @Override
    public Request reject(Long userId, Long eventId, Long reqId) {
        Event event = getEventOrThrow(eventId);
        if (!event.getRequestModeration()) {
            throw new ConflictException("The event does not require confirmation", "request moderation is false");
        }
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ValidationException("Only initiator can confirm the request", format("InitiatorId=%d", userId));
        }
        Request request = event.getRequests().stream().filter(r -> Objects.equals(r.getId(), reqId)).findFirst()
                .orElseThrow(() -> new ValidationException("Wrong event for the request", format("EventId=%d, RequestId=%d", eventId, reqId)));

        event.getRequests().remove(request);
        eventRepository.save(event);
        request.setState(RequestState.REJECTED);
        return request;
    }

    @Override
    public Event getPublishedById(Long eventId) {
        return eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event not found or not published yet", format("Id=%d", eventId)));
    }

    @Override
    public List<Event> getAllPublished(String text, List<Long> categories, Boolean paid, Boolean onlyAvailable,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        List<Event> events = eventRepository.findAllByParameters(
                null, List.of(EventState.PUBLISHED), categories, paid, rangeStart, rangeEnd, text, onlyAvailable, from, size
        );
        if (Objects.nonNull(onlyAvailable) && onlyAvailable) {
            events = events.stream().filter(e -> !isRequestLimit(e)).collect(Collectors.toList());
        }
        return events;
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
                .orElseThrow(() -> new NotFoundException("Event not found", format("Id=%d", eventId)));
    }

    private void validateEventDateOrThrow(LocalDateTime eventDate) {
        LocalDateTime minTime = LocalDateTime.now().plusMinutes(minMinutesToEvent);
        if (minTime.isAfter(eventDate)) {
            throw new ValidationException(format("Must be minimum %d minutes before event start", minMinutesToEvent),
                    eventDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }
}
