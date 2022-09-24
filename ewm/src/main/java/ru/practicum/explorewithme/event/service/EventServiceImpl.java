package ru.practicum.explorewithme.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.common.OffsetLimitPageable;
import ru.practicum.explorewithme.event.db.Event;
import ru.practicum.explorewithme.event.db.EventRepository;
import ru.practicum.explorewithme.event.dto.EventState;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.users.db.User;
import ru.practicum.explorewithme.users.factory.UserFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final EventMapper mapper;

    private final UserFactory userFactory;

    @Value("${app.minTimeToEventMinutes}")
    private int minTimeToEventMinutes;

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
                .orElseThrow(() -> new NotFoundException("No Event found", String.format("EventId=%d, UserId=%d", eventId, userId)));
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
        Pageable pageable = OffsetLimitPageable.of(from, size);
        return eventRepository.findAllByParameters(users, states, categories, rangeStart, rangeEnd, pageable);
    }

    @Override
    public Event approve(Long eventId, String stateString) {
        if ("reject".equals(stateString) || "publish".equals(stateString)) {
            EventState state = EventState.valueOf((stateString + "ed").toUpperCase());
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(()-> new NotFoundException("Event not found", String.format("id=%d", eventId)));
            event.setState(state);
            return eventRepository.save(event);
        }
        throw new ValidationException("State must be reject or publish", stateString);
    }

    private void validateEventDateOrThrow(LocalDateTime eventDate) {
        LocalDateTime minTime = LocalDateTime.now().plusMinutes(minTimeToEventMinutes);
        if (minTime.isAfter(eventDate)) {
            throw new ValidationException(String.format("Must be minimum %d minutes before event start", minTimeToEventMinutes),
                    eventDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }
}
