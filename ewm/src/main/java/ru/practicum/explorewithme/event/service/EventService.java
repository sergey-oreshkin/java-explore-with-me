package ru.practicum.explorewithme.event.service;

import ru.practicum.explorewithme.event.db.Event;
import ru.practicum.explorewithme.event.dto.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    Event create(Event event);

    Event update(Event event);

    List<Event> getAll(Long userId, Integer from, Integer size);

    Event get(Long userId, Long eventId);

    Event cancel(Long userId, Long eventId);

    List<Event> getAll(List<Long> users, List<EventState> states, List<Long> categories,
                       LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    Event approve(Long eventId, String stateString);
}
