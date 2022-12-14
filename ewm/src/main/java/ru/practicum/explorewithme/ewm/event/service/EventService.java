package ru.practicum.explorewithme.ewm.event.service;

import ru.practicum.explorewithme.ewm.event.db.Event;
import ru.practicum.explorewithme.ewm.event.dto.EventState;
import ru.practicum.explorewithme.ewm.request.db.Request;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    Event create(Event event);

    Event update(Event event, boolean isAdmin);

    List<Event> getAll(Long userId, Integer from, Integer size);

    Event get(Long userId, Long eventId);

    Event cancel(Long userId, Long eventId);

    List<Event> getAll(List<Long> users, List<EventState> states, List<Long> categories,
                       LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    Event setEventState(Long eventId, EventState state, String comment);

    List<Request> getRequests(Long userId, Long eventId);

    Request confirm(Long userId, Long eventId, Long reqId);

    Request reject(Long userId, Long eventId, Long reqId);

    Event getPublishedById(Long eventId);

    List<Event> getAllPublished(String text, List<Long> categories, Boolean paid, Boolean onlyAvailable,
                                LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    boolean isRequestLimit(Event event);
}
